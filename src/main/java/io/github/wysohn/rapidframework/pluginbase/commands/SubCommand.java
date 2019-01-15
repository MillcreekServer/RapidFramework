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
package io.github.wysohn.rapidframework.pluginbase.commands;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class SubCommand {
    protected final PluginBase base;
    protected final String name;

    protected String parent = null;

    protected String[] aliases;
    protected String permission;
    protected Language permissionDeniedMessage = DefaultLanguages.General_NotEnoughPermission;
    protected Language description;
    protected Language[] usage;
    private Map<Predicate<CommandSender>, Language> predicates = new HashMap<>();
    private List<ArgumentMapper<?>> argumentMappers = new ArrayList<>();
    private CommandAction<ConsoleCommandSender> action_console;
    private CommandAction<Player> action_player;

    protected ChatColor commandColor = ChatColor.GOLD;

    private int arguments = -1;

    private SubCommand(PluginBase base, String name){
        this.base = base;
        this.name = name;
    }

    private SubCommand(PluginBase base, String name, int numArgs){
        this(base, name);
        this.arguments = numArgs;
    }

    protected SubCommand(PluginBase base, String permission, Language description, Language[] usage, int arguments,
                      String name, String... aliases) {
        this.base = base;
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.description = description;
        this.usage = usage;
        this.arguments = arguments;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Language getPermissionDeniedMessage() {
        return permissionDeniedMessage;
    }

    public void setPermissionDeniedMessage(Language permissionDeniedMessage) {
        this.permissionDeniedMessage = permissionDeniedMessage;
    }

    public ChatColor getCommandColor() {
        return commandColor;
    }

    public int getArguments() {
        return arguments;
    }

    protected void setArguments(int arguments) {
        this.arguments = arguments;
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (arguments != -1 && args.length != arguments)
            return false;
        
        for(Entry<Predicate<CommandSender>, Language> entry : this.predicates.entrySet()) {
        	Predicate<CommandSender> pred = entry.getKey();
        	Language failLang = entry.getValue();
        	
        	if(!pred.test(sender)) {
        		base.sendMessage(sender, failLang);
        		return true;
        	}
        }

        if (sender == null || sender instanceof ConsoleCommandSender) {
            return executeConsole((ConsoleCommandSender) sender, args);
        } else {
            Player player = (Player) sender;
            if (player.isOp()) {
                return executeOp(player, args);
            } else {
                return executeUser(player, args);
            }
        }
    }

    protected boolean executeConsole(ConsoleCommandSender sender, Object[] args) {
        if(action_console != null)
            action_console.execute(sender, IntStream.range(args.length));

        base.getLogger().info("Not allowed to execute from Console.");
        return true;
    }

    protected boolean executeOp(Player op, Object[] args) {
        return executeUser(op, args);
    }

    protected boolean executeUser(Player player, String[] args) {
    	if(action_player != null)
    		action_player.execute(player, args);
    	
    	return true;
    }

    public boolean testPermission(CommandSender sender) {
        if (permission == null)
            return true;

        if (!testPermissionSilent(sender)) {
            base.sendMessage(sender, permissionDeniedMessage);
            return false;
        }

        return true;
    }

    public boolean testPermissionSilent(CommandSender sender) {
        if (permission == null)
            return true;

        return sender.hasPermission(permission);
    }

    /*
     * @Override public int hashCode() { final int prime = 31; int result = 1;
     * result = prime * result + ((getName() == null) ? 0 :
     * getName().hashCode()); return result; }
     * 
     * @Override public boolean equals(Object obj) { if (this == obj) return
     * true; if (obj == null) return false; if (getClass() != obj.getClass())
     * return false; SubCommand other = (SubCommand) obj; if (getName() == null)
     * { if (other.getName() != null) return false; } else if
     * (!getName().equals(other.getName())) return false; return true; }
     */

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public Language getDescription() {
        return description;
    }

    public Language[] getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return name;
    }

    @FunctionalInterface
    public interface CommandAction<T extends CommandSender>{
        boolean execute(T sender, String[] args);
    }
    
    @FunctionalInterface
    public interface ArgumentMapper<R>{
    	public R apply(String arg) throws InvalidArgumentException;
    	
    	static ArgumentMapper<String> IDENTITY = (arg) -> arg;
    }
    
    @SuppressWarnings("serial")
	public class InvalidArgumentException extends Exception{
    	private final Language lang;

		public InvalidArgumentException(Language lang) {
			super();
			this.lang = lang;
		}
    }

    public static class Builder{
        private SubCommand command;

        private Builder(PluginBase base, String cmd, int numArgs){
            command = new SubCommand(base, cmd, numArgs){};
            command.permission = base.commandExecutor.mainCommand+"."+cmd;
        }

        public static Builder forCommand(String cmd, PluginBase base){
            return new Builder(base, cmd, 0);
        }

        public static Builder forCommand(String cmd, PluginBase base, int numArgs){
            return new Builder(base, cmd, numArgs);
        }

        public Builder childOf(String cmd){
            command.parent = cmd;
            return this;
        }

        public Builder withAlias(String... alias){
            command.aliases = alias;
            return this;
        }

        public Builder withPermission(String permission){
            command.permission = permission;
            return this;
        }

        public Builder withDescription(Language description){
            command.description = description;
            return this;
        }

        public Builder withUsage(Language... usage){
            command.usage = usage;
            return this;
        }

        public Builder withColor(ChatColor color){
            command.commandColor = color;
            return this;
        }

        public Builder actOnConsole(CommandAction<ConsoleCommandSender> action){
            command.action_console = action;
            return this;
        }

        public Builder actOnPlayer(CommandAction<Player> action){
            command.action_player = action;
            return this;
        }
        
        public Builder addRequirement(Predicate<CommandSender> predicate, Language failLang) {
        	command.predicates.put(predicate, failLang);
        	return this;
        }
        
        public Builder addArgumentMapper(int index, ArgumentMapper<?> mapper) {
        	if(mapper == null)
        		throw new RuntimeException("Cannot use null for mapper! Use ArgumentMapper.IDENTITY if "
        				+ "mapping is not required.");
        	
        	while(command.argumentMappers.size() <= index)
        		command.argumentMappers.add(ArgumentMapper.IDENTITY);
        	command.argumentMappers.add(mapper);
        	return this;
        }

        public SubCommand create(){
            return command;
        }
    }
}
