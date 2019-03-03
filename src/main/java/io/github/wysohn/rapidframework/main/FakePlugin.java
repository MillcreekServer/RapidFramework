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
package io.github.wysohn.rapidframework.main;

import io.github.wysohn.rapidframework.main.nms.entity.INmsEntityManager;
import io.github.wysohn.rapidframework.main.nms.particle.INmsParticleSender;
import io.github.wysohn.rapidframework.main.nms.world.BlockFilter;
import io.github.wysohn.rapidframework.main.nms.world.INmsWorldManager;
import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginConfig;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

//just a fake plugin
public class FakePlugin extends PluginBase {
    public static Plugin instance;

    public static INmsWorldManager nmsWorldManager;
    public static INmsEntityManager nmsEntityManager;
    public static INmsParticleSender nmsParticleSender;

    public FakePlugin() {
        super("rapidframework", "rapidframework.admin");
    }

    @Override
	protected PluginConfig initConfig() {
		return new FakePluginConfig();
	}

	@Override
    protected void preEnable() {
        instance = this;

        String packageName = getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            initWorldNms(version);
            initEntityrNms(version);
            initParticleNms(version);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            getLogger().severe("Version [" + version + "] is not supported by this plugin.");
            this.setEnabled(false);
        }
    }

    @Override
	protected void postEnable() {

	}

	@Override
	protected void initLangauges(List<Language> languages) {
		
	}

	@Override
	protected void initCommands(List<SubCommand> subcommands) {
		
	}

	@Override
	protected void initAPIs(Map<String, Class<? extends APISupport>> apisupports) {
		
	}

	@Override
	protected void initManagers(List<PluginManager<? extends PluginBase>> pluginmanagers) {
		
	}

	private static final String packageName = "io.github.wysohn.rapidframework.main.nms";

    private void initWorldNms(String version)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(packageName + ".world." + version + "." + "NmsChunkManager");
        nmsWorldManager = (INmsWorldManager) clazz.newInstance();
    }

    private void initEntityrNms(String version)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(packageName + ".entity." + version + "." + "NmsEntityProvider");
        nmsEntityManager = (INmsEntityManager) clazz.newInstance();
    }

    private void initParticleNms(String version)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(packageName + ".particle." + version + "." + "NmsParticleSender");
        nmsParticleSender = (INmsParticleSender) clazz.newInstance();
    }

    private static Set<Integer> ores = new HashSet<Integer>() {
        {
            for (Material mat : Material.values())
                if (mat.name().endsWith("_ORE"))
                    add(mat.getId());
        }
    };
    private static UUID temp = UUID.randomUUID();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !((Player) sender).isOp())
            return true;

        if (!label.equals("rapidframework"))
            return true;

        try {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("glow")) {
                    Player player = (Player) sender;
                    int x = player.getLocation().getBlockX();
                    int y = player.getLocation().getBlockY();
                    int z = player.getLocation().getBlockZ();

                    nmsParticleSender.showGlowingBlock(new Player[] { player }, -700, temp, x, y, z);
                } else if (args[0].equalsIgnoreCase("del")) {
                    Player player = (Player) sender;

                    nmsEntityManager.destroyEntity(new Player[] { player }, new int[] { -700 });
                } else if (args[0].equalsIgnoreCase("color")) {
                    Player player = (Player) sender;

                    nmsEntityManager.sendTeamColor(new Player[] { player }, "temp", ChatColor.RED + "",
                            new HashSet<String>() {
                                {
                                    add(temp.toString());
                                }
                            }, 2);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("chunk")) {
                    Player player = (Player) sender;
                    int i = Integer.parseInt(args[1]);
                    int j = Integer.parseInt(args[2]);

                    nmsWorldManager.regenerateChunk(player.getWorld(), i, j, new BlockFilter() {
                        @Override
                        public boolean allow(int blockID, byte data) {
                            return ores.contains(blockID);
                        }
                    });
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("chunk")) {
                    World world = Bukkit.getWorld(args[3]);
                    int i = Integer.parseInt(args[1]);
                    int j = Integer.parseInt(args[2]);

                    nmsWorldManager.regenerateChunk(world, i, j, new BlockFilter() {
                        @Override
                        public boolean allow(int blockID, byte data) {
                            return ores.contains(blockID);
                        }
                    });
                }
            }/* else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("prop")) {
                    if(args[1].equals("show")) {
                        printProp(sender, 0, props);
                    }else if(args[1].equals("start")) {
                        ManagerPropertyEdit m = this.getManager(ManagerPropertyEdit.class);
                        m.startEdit((Player) sender, DefaultLanguages.General_Header, props, new ConversationAbandonedListener() {

                            @Override
                            public void conversationAbandoned(ConversationAbandonedEvent arg0) {
                                printProp(sender, 0, (Map<String, Object>) arg0.getContext()
                                        .getSessionData(ManagerPropertyEdit.PROPERTY_SESSIONDATANAME));
                            }

                        });
                    }
                }
            }*/
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }

        return true;
    }

    private static void printProp(CommandSender sender, int level, Map<String, Object> map) {
        for(Entry<String, Object> entry : map.entrySet()) {
            if(entry.getValue() instanceof Map) {
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < level * 4; i++)
                    builder.append(' ');
                sender.sendMessage(builder.toString()+entry.getKey()+":");
                printProp(sender, level + 1, (Map<String, Object>) entry.getValue());
            }else {
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < level * 4; i++)
                    builder.append(' ');
                sender.sendMessage(builder.toString()+String.valueOf(entry));
            }
        }
    }

}
