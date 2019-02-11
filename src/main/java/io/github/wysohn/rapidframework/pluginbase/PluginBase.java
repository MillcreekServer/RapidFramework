/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework.pluginbase;

import io.github.wysohn.rapidframework.main.FakePlugin;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.PreParseHandle;
import io.github.wysohn.rapidframework.pluginbase.commands.SubCommand;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework.pluginbase.manager.*;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.ManagerGUI;
import io.github.wysohn.rapidframework.pluginbase.manager.tasks.ManagerSequentialTask;
import io.github.wysohn.rapidframework.pluginbase.manager.tasks.ManagerVolatileTask;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Always register commands, managers, APIs, and languages.
 *
 * @author wysohn
 *
 */
public abstract class PluginBase extends JavaPlugin {
    final Map<Class<? extends PluginManager>, PluginManager> pluginManagers = new HashMap<Class<? extends PluginManager>, PluginManager>();
    private Map<String, PluginManager> pluginManagersString = new HashMap<String, PluginManager>();
    
    private PluginConfig config;
    public PluginLanguage lang;
    public PluginCommandExecutor commandExecutor;
    public Map<String, PluginCommandExecutor> commandExecutors;
    public PluginAPISupport APISupport;

    private String[] mainCommand;
    private String adminPermission;

    public static void runAsynchronously(Runnable run) {
        Bukkit.getScheduler().runTaskAsynchronously(FakePlugin.instance, run);
    }

    /**
     * Do not call this contstructor.
     */
    protected PluginBase() {
        throw new RuntimeException(
                "Please override default constructor in order to establish PluginBase. This overriden constructor"
                        + " should also call super constructor(String, String).");
    }

    public PluginBase(String mainCommand, String adminPermission) {
        this(new String[] { mainCommand }, adminPermission);
    }

    public PluginBase(String[] mainCommand, String adminPermission) {
        this.mainCommand = mainCommand;
        this.adminPermission = adminPermission;

        registerManager(new ManagerCustomPermission(this, PluginManager.FASTEST_PRIORITY));
        
        registerManager(ManagerPlayerLocation.getSharedInstance(this));
        registerManager(new ManagerAreaSelection(this, PluginManager.NORM_PRIORITY));
        registerManager(new ManagerVolatileTask(this, PluginManager.NORM_PRIORITY));
        registerManager(new ManagerSequentialTask(this, PluginManager.NORM_PRIORITY));
        registerManager(new ManagerTargetBlock(this, PluginManager.NORM_PRIORITY));
        registerManager(new ManagerPropertyEdit(this, PluginManager.NORM_PRIORITY));
        registerManager(new ManagerGUI(this, PluginManager.NORM_PRIORITY));
        registerManager(new ManagerGroup(this, PluginManager.NORM_PRIORITY));     
    }

    private void initiatePluginProcedures() {
    	config = this.initConfig();
        try {
            if (config != null && this.isEnabled())
            	config.onEnable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While loading config:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
            this.setEnabled(false);
        }
        
        if(config.Plugin_Debugging) {
        	this.getLogger().setLevel(Level.FINE);
        } else {
        	this.getLogger().setLevel(Level.INFO);
        }
        
        try {
            if (this.isEnabled())
                lang.onEnable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While loading lang:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
            this.setEnabled(false);
        }

        try {
            if (this.isEnabled()) {
                for (PluginCommandExecutor executor : commandExecutors.values())
                    executor.onEnable(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While loading command executor:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
            this.setEnabled(false);
        }

        try {
            if (this.isEnabled())
                APISupport.onEnable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While loading APISupport:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
            this.setEnabled(false);
        }

        Map<Integer, Set<PluginManager>> map = new TreeMap<Integer, Set<PluginManager>>();
        for (int i = PluginManager.FASTEST_PRIORITY; i <= PluginManager.SLOWEST_PRIORITY; i++) {
            map.put(i, new HashSet<PluginManager>());
        }

        for (Entry<Class<? extends PluginManager>, PluginManager> entry : pluginManagers.entrySet()) {
            Set<PluginManager> set = map.get(entry.getValue().getLoadPriority());
            set.add(entry.getValue());
        }

        for (Entry<Integer, Set<PluginManager>> entry : map.entrySet()) {
            Set<PluginManager> managers = entry.getValue();

            for (PluginManager manager : managers) {
                try {
                	manager.onInitInternal();
                    manager.onEnable();
                } catch (Exception e) {
                    e.printStackTrace();
                    this.getLogger().severe("While Enabling [" + manager.getClass().getSimpleName() + "]:");
                    this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
                    this.setEnabled(false);

                    this.getLogger().info(lang.parseFirstString(DefaultLanguages.Plugin_WillBeDisabled));
                    return;
                }

                if (manager instanceof Listener && this.isEnabled()) {
                	Bukkit.getPluginManager().registerEvents((Listener) manager, this);
                }
            }
        }
    }

    private void finalizeDisableProcedures() {
        try {
            if (config != null)
                config.onDisable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While disabling config:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }

        try {
            if (lang != null)
                lang.onDisable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While disabling lang:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }

        try {
            for (PluginCommandExecutor executor : commandExecutors.values())
                executor.onDisable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While disabling command executor:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }

        try {
            if (APISupport != null)
                APISupport.onDisable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While disabling APISupport:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }

        Map<Integer, Set<PluginManager>> map = new TreeMap<Integer, Set<PluginManager>>();
        for (int i = PluginManager.FASTEST_PRIORITY; i <= PluginManager.SLOWEST_PRIORITY; i++) {
            map.put(i, new HashSet<PluginManager>());
        }

        for (Entry<Class<? extends PluginManager>, PluginManager> entry : pluginManagers.entrySet()) {
            Set<PluginManager> set = map.get(entry.getValue().getLoadPriority());
            set.add(entry.getValue());
        }

        for (Entry<Integer, Set<PluginManager>> entry : map.entrySet()) {
            Set<PluginManager> managers = entry.getValue();

            for (PluginManager manager : managers) {
                try {
                	manager.onDisableInternal();
                    manager.onDisable();
                } catch (Exception e) {
                    e.printStackTrace();
                    this.getLogger().severe("While Enabling [" + manager.getClass().getSimpleName() + "]:");
                    this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
                    this.setEnabled(false);

                    this.getLogger().info(lang.parseFirstString(DefaultLanguages.Plugin_WillBeDisabled));
                    return;
                }
            }
        }
    }

    public void reloadPluginProcedures() {
        try {
        	if(config != null)
        		config.onReload(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While reloading config:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }
        
        if(config.Plugin_Debugging) {
        	this.getLogger().setLevel(Level.FINE);
        } else {
        	this.getLogger().setLevel(Level.INFO);
        }

        try {
            lang.onReload(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While reloading lang:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }

        try {
            for (PluginCommandExecutor executor : commandExecutors.values())
                executor.onReload(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While reloading command executor:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }

        try {
            APISupport.onReload(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While reloading APISupport:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
        }

        Map<Integer, Set<PluginManager>> map = new TreeMap<Integer, Set<PluginManager>>();
        for (int i = PluginManager.FASTEST_PRIORITY; i <= PluginManager.SLOWEST_PRIORITY; i++) {
            map.put(i, new HashSet<PluginManager>());
        }

        for (Entry<Class<? extends PluginManager>, PluginManager> entry : pluginManagers.entrySet()) {
            Set<PluginManager> set = map.get(entry.getValue().getLoadPriority());
            set.add(entry.getValue());
        }

        for (Entry<Integer, Set<PluginManager>> entry : map.entrySet()) {
            Set<PluginManager> managers = entry.getValue();

            for (PluginManager manager : managers) {
                try {
                    if (this.isEnabled()) {
                    	manager.onReloadInternal();
                        manager.onReload();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.getLogger().severe("While Enabling [" + manager.getClass().getSimpleName() + "]:");
                    this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
                    this.setEnabled(false);

                    this.getLogger().info(lang.parseFirstString(DefaultLanguages.Plugin_WillBeDisabled));
                    return;
                }
            }
        }
    }

    @Override
    public void onEnable() {
    	config = this.initConfig();
    	
        try {
            config.onEnable(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("While loading config:");
            this.getLogger().severe(e.getClass().getSimpleName() + "@" + e.getMessage());
            this.setEnabled(false);
        }

        String def = config.Plugin_Language_Default;
        Set<String> list = new HashSet<String>();
        list.addAll(config.Plugin_Language_List);

        this.lang = new PluginLanguage(list, def);
        this.commandExecutors = new HashMap<>();
        for (int i = 0; i < mainCommand.length; i++)
            this.commandExecutors.put(mainCommand[i], new PluginCommandExecutor(mainCommand[i], adminPermission));
        this.commandExecutor = this.commandExecutors.get(mainCommand[0]);
        this.APISupport = new PluginAPISupport();

        preEnable();
        
        initLangauges().forEach((language) -> lang.registerLanguage(language));
        initCommands().forEach((cmd) -> {
            if(cmd.getParent() != null)
                commandExecutors.get(cmd.getParent()).addCommand(cmd);
            else
                commandExecutor.addCommand(cmd);
        });
        initAPIs().forEach((entry)->APISupport.hookAPI(entry.getKey(), entry.getValue()));
        initManagers().forEach(this::registerManager);

        initiatePluginProcedures();
        
        postEnable();
    }
    
    /**
     * Called before managers, language, commands, etc. are initialized
     */
    protected abstract void preEnable();
    
    /**
     * Called after managers, language, commands, etc. are initialized
     */
    protected abstract void postEnable();
    
    /**
     * Override this to use custom version of config.
     * Default one will be used otherwise.
     * @return the custom config that will be used by plugin.
     */
    protected PluginConfig initConfig() {
		return new PluginConfig();
    }
    protected Stream<Language> initLangauges(){
        return Stream.of();
    }
    protected Stream<SubCommand> initCommands(){
        return Stream.of();
    }
    protected Stream<Entry<String, Class<? extends PluginAPISupport.APISupport>>> initAPIs(){
        return Stream.of();
    }
    protected Stream<PluginManager<? extends  PluginBase>> initManagers(){
        return Stream.of();
    }

    @Override
    public void onDisable() {
        finalizeDisableProcedures();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PluginCommandExecutor executor = commandExecutors.get(command.getName());
        if (executor == null)
            return true;

        return executor.onCommand(sender, command, label, args);
    }

    public void sendMessage(CommandSender sender, Language language) {
        if (sender == null)
            return;

        if (sender instanceof Player) {
            sendMessage((Player) sender, language);
        } else {
            for (String msg : lang.parseStrings(language)) {
                sendMessage(sender, msg, 1000);
            }
        }
    }

    public void sendMessage(Player player, Language language) {
        if (player == null)
            return;

        String localeSimplified = "en";
        try {
            localeSimplified = FakePlugin.nmsEntityManager.getLocale(player);
        } catch (Exception e) {
            // silently fail
        } finally {
            for (String msg : lang.parseStrings(player, language, localeSimplified))
                sendMessage(player, msg, 1000);
        }
    }

    private static void sendMessage(CommandSender sender, String msg, int limit) {
        if (msg.length() <= limit) {
            sender.sendMessage(msg);
        } else {
            int count = (int) Math.ceil((double) msg.length() / limit);
            for (int i = 0; i < count; i++) {
                int index = i * limit;
                if (i < count - 1) {
                    sender.sendMessage(msg.substring(index, index + limit));
                } else {
                    sender.sendMessage(msg.substring(index, index + msg.length() % limit));
                }
            }
        }
    }

    public void broadcast(Language language) {
        broadcast(language, null, null);
    }

    public void broadcast(Language language, Predicate<Player> filter) {
        broadcast(language, filter, null);
    }

    public void broadcast(Language language, PreParseHandle parseHandle) {
        broadcast(language, null, parseHandle);
    }

    public void broadcast(Language language, Predicate<Player> filter, PreParseHandle parseHandle) {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(filter != null && !filter.test(player))
                continue;

            if(parseHandle != null)
                parseHandle.onParse(lang, player);

            sendMessage(player, language);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends PluginConfig> T getPluginConfig() {
        return (T) config;
    }

    public Map<Class<? extends PluginManager>, PluginManager> getPluginManagers() {
        return pluginManagers;
    }

    public void registerManager(PluginManager manager) {
        pluginManagers.put(manager.getClass(), manager);
        pluginManagersString.put(manager.getClass().getSimpleName(), manager);
    }

    public <T extends PluginManager> T getManager(Class<? extends PluginManager> clazz) {
        return (T) pluginManagers.get(clazz);
    }
    
    public <T extends PluginManager> T getManager(String managerClassSimpleName) {
    	return (T) pluginManagersString.get(managerClassSimpleName);
    }

    public static void main(String[] ar) {
        String msg = "123";
        int limit = 2;
        if (msg.length() <= limit) {
            System.out.println(msg);
        } else {
            int count = (int) Math.ceil((double) msg.length() / limit);
            for (int i = 0; i < count; i++) {
                int index = i * limit;
                if (i < count - 1) {
                    System.out.println(msg.substring(index, index + limit));
                } else {
                    System.out.println(msg.substring(index, index + msg.length() % limit));
                }
            }
        }
    }
}
