package io.github.wysohn.rapidframework2.bukkit.testutils;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitWrapper;
import io.github.wysohn.rapidframework2.bukkit.testutils.impl.CraftInventoryPlayer;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, Plugin.class})
@PowerMockIgnore({"javax.management.*"})
public class AbstractBukkitTest {
    protected static Logger log = LoggerFactory.getLogger(AbstractBukkitTest.class);

    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
    }

    protected static final InetAddress INET_ADDR = InetAddress.getLoopbackAddress();
    protected static final Map<UUID, Player> UUID_PLAYER_MAP = new HashMap<>();
    protected static final Map<String, Player> NAME_PLAYER_MAP = new HashMap<>();
    protected static final Map<UUID, PlayerInventory> UUID_PLAYER_INVENTORY_MAP = new HashMap<>();

    private Player player;
    protected final UUID PLAYER_UUID = UUID.randomUUID();
    protected final String PLAYER_NAME = "user";
    protected final PlayerInventory PLAYER_INVENTORY = new CraftInventoryPlayer();

    private PluginManager pluginManager;
    private ItemFactory itemFactory;
    protected BukkitScheduler bukkitScheduler;

    protected Location mockLocation;
    protected World mockWorld;
    protected String worldName = "world";
    protected double x = 0.0;
    protected double y = 0.0;
    protected double z = 0.0;

    @Before
    public void setupBukkit() {
        player = mock(Player.class);

        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(player.getDisplayName()).thenReturn(PLAYER_NAME);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(player.getLocale()).thenReturn(Locale.ENGLISH.getDisplayName());
        when(player.getInventory()).thenReturn(PLAYER_INVENTORY);
        when(player.isConversing()).thenReturn(false);

        doAnswer(invocation -> {
            String[] msgs = (String[]) invocation.getArguments()[0];
            log.info(PLAYER_NAME + " got message: " + Arrays.toString(msgs));
            return null;
        }).when(player).sendMessage(any(String[].class));

        mockLocation = mock(Location.class);
        mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn(worldName);
        when(player.getLocation()).thenReturn(mockLocation);
        when(mockLocation.getWorld()).thenReturn(mockWorld);
        when(mockLocation.getX()).thenReturn(x);
        when(mockLocation.getBlockX()).thenReturn((int) x);
        when(mockLocation.getY()).thenReturn(y);
        when(mockLocation.getBlockY()).thenReturn((int) y);
        when(mockLocation.getZ()).thenReturn(z);
        when(mockLocation.getBlockZ()).thenReturn((int) z);
        doAnswer(invocation -> {
            Location loc = (Location) invocation.getArguments()[0];
            x = loc.getX();
            y = loc.getY();
            z = loc.getZ();
            return null;
        }).when(player).teleport(any(Location.class));

        UUID_PLAYER_MAP.put(PLAYER_UUID, player);
        NAME_PLAYER_MAP.put(PLAYER_NAME, player);
        log.info("Default sender is set " + player);

        pluginManager = mock(PluginManager.class);
        itemFactory = mock(ItemFactory.class);

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getOfflinePlayer(anyString())).then(ans -> Optional.of(ans)
                .map(InvocationOnMock::getArguments)
                .map(args -> args[0])
                .map(String.class::cast)
                .map(NAME_PLAYER_MAP::get)
                .orElseGet(() -> {
                    log.info("getOfflinePlayer(UUID) returned null.");
                    return null;
                }));
        Mockito.when(Bukkit.getOfflinePlayer(any(UUID.class))).then(ans -> Optional.of(ans)
                .map(InvocationOnMock::getArguments)
                .map(args -> args[0])
                .map(UUID.class::cast)
                .map(UUID_PLAYER_MAP::get)
                .orElseGet(() -> {
                    log.info("getOfflinePlayer(UUID) returned null.");
                    return null;
                }));
        Mockito.when(Bukkit.getPlayer(anyString())).then(ans -> Optional.of(ans)
                .map(InvocationOnMock::getArguments)
                .map(args -> args[0])
                .map(String.class::cast)
                .map(NAME_PLAYER_MAP::get)
                .orElseGet(() -> {
                    log.info("getPlayer(String) returned null.");
                    return null;
                }));
        Mockito.when(Bukkit.getPlayer(any(UUID.class))).then(ans -> Optional.of(ans)
                .map(InvocationOnMock::getArguments)
                .map(args -> args[0])
                .map(UUID.class::cast)
                .map(UUID_PLAYER_MAP::get)
                .orElseGet(() -> {
                    log.info("getPlayer(UUID) returned null.");
                    return null;
                }));
        Mockito.when(Bukkit.getPluginManager()).thenReturn(pluginManager);
        Mockito.when(Bukkit.getItemFactory()).thenReturn(itemFactory);

        bukkitScheduler = mock(BukkitScheduler.class);
        when(bukkitScheduler.callSyncMethod(any(Plugin.class), any(Callable.class)))
                .then(invocation -> {
                    FutureTask task = new FutureTask((Callable) invocation.getArguments()[1]);
                    task.run();
                    return task;
                });
        when(bukkitScheduler.runTask(any(Plugin.class), any(Runnable.class)))
                .then(invocation -> {
                    ((Runnable) invocation.getArguments()[1]).run();
                    return null;
                });
        Mockito.when(Bukkit.getScheduler()).thenReturn(bukkitScheduler);

        java.util.logging.Logger mockLogger = mock(java.util.logging.Logger.class);
        doAnswer(invocation -> {
            Level level = (Level) invocation.getArguments()[0];
            String message = (String) invocation.getArguments()[1];

            log.info(level + ": " + message);
            return null;
        }).when(mockLogger).log(any(Level.class), anyString());
        Mockito.when(Bukkit.getLogger()).thenReturn(mockLogger);
    }

    public PluginManager getMockPluginManager() {
        return pluginManager;
    }

    public ItemFactory getItemFactory() {
        return itemFactory;
    }

    /**
     * Returns atomic Player instance
     *
     * @return
     */
    protected Player player() {
        return player;
    }

    protected Player player(UUID uuid, String name) {
        Player p = mock(Player.class);
        when(p.getUniqueId()).thenReturn(uuid);
        when(p.getName()).thenReturn(name);
        when(p.getDisplayName()).thenReturn(name);
        when(p.hasPermission(anyString())).thenReturn(true);
        when(p.getLocale()).thenReturn(Locale.ENGLISH.getDisplayName());
        when(p.getInventory()).then(invocation -> UUID_PLAYER_INVENTORY_MAP.get(uuid));
        doAnswer(invocation -> {
            String[] msgs = (String[]) invocation.getArguments()[0];
            log.info(name + " got message: " + Arrays.toString(msgs));
            return null;
        }).when(p).sendMessage(any(String[].class));

        NAME_PLAYER_MAP.computeIfAbsent(name, (k) -> p);
        UUID_PLAYER_INVENTORY_MAP.computeIfAbsent(uuid, (k) -> new CraftInventoryPlayer());
        return UUID_PLAYER_MAP.computeIfAbsent(uuid, (k) -> p);
    }

    protected ICommandSender wrap(Player player){
        return BukkitWrapper.player(player);
    }

    protected UUID uuid() {
        return PLAYER_UUID;
    }

    protected String name() {
        return PLAYER_NAME;
    }

    protected void location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected void location(String name, double x, double y, double z) {
        this.worldName = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected Location location() {
        return mockLocation;
    }

    @SafeVarargs
    protected final <T> Consumer<T> invokeMethods(ThrowingConsumer<T>... fn) {
        return manager -> {
            try {
                for (ThrowingConsumer<T> consumer : fn) {
                    consumer.accept(manager);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public interface ThrowingConsumer<T>{
        void accept(T t) throws Exception;
    }

    public static Function<PluginMainTestBuilder, Boolean> runSubCommand(Player player2, String... args) {
        return (context) -> {
            context.getMain().comm().runSubCommand(BukkitWrapper.player(player2), args);
            return true;
        };
    }

    public static PluginMainTestBuilder.MockEventProvider loginEvent(String playerName, UUID playerUuid){
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        InetAddress finalAddress = address;
        return context -> new AsyncPlayerPreLoginEvent(playerName, finalAddress, playerUuid);
    }

    public static PluginMainTestBuilder.MockEventProvider joinEvent(Player mockPlayer){
        return context -> new PlayerJoinEvent(mockPlayer, mockPlayer+" join event.");
    }

    public static PluginMainTestBuilder.MockEventProvider quitEvent(Player mockPlayer){
        return context -> new PlayerQuitEvent(mockPlayer, mockPlayer+" quit event.");
    }
}
