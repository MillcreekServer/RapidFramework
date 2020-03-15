package io.github.wysohn.rapidframework2.bukkit.testutils;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitPlayer;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
import java.util.*;
import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class})
@PowerMockIgnore({"javax.management.*"})
public class AbstractBukkitTest {
    protected static Logger log = LoggerFactory.getLogger(AbstractBukkitTest.class);

    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
    }

    protected static final InetAddress INET_ADDR = InetAddress.getLoopbackAddress();
    protected static final Map<UUID, Player> UUID_PLAYER_MAP = new HashMap<>();
    protected static final Map<String, Player> NAME_PLAYER_MAP = new HashMap<>();

    private Player player;
    protected final UUID PLAYER_UUID = UUID.randomUUID();
    protected final String PLAYER_NAME = "user";

    @Before
    public void setupBukkit() {
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(player.getDisplayName()).thenReturn(PLAYER_NAME);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(player.getLocale()).thenReturn(Locale.ENGLISH.getDisplayName());
        doAnswer(invocation -> {
            String[] msgs = (String[]) invocation.getArguments()[0];
            log.info(PLAYER_NAME + " got message: " + Arrays.toString(msgs));
            return null;
        }).when(player).sendMessage(any(String[].class));

        UUID_PLAYER_MAP.put(PLAYER_UUID, player);
        NAME_PLAYER_MAP.put(PLAYER_NAME, player);
        log.info("Default sender is set " + player);

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
        doAnswer(invocation -> {
            String[] msgs = (String[]) invocation.getArguments()[0];
            log.info(name + " got message: " + Arrays.toString(msgs));
            return null;
        }).when(p).sendMessage(any(String[].class));

        NAME_PLAYER_MAP.computeIfAbsent(name, (k) -> p);
        return UUID_PLAYER_MAP.computeIfAbsent(uuid, (k) -> p);
    }

    protected ICommandSender wrap(Player player){
        return new BukkitPlayer(player.getUniqueId()).setSender(player);
    }

    protected UUID uuid() {
        return PLAYER_UUID;
    }

    protected String name() {
        return PLAYER_NAME;
    }

    @SafeVarargs
    protected final <T> Consumer<T> invokeMethods(ThrowingConsumer<T>... fn) {
        return manager -> {
            try {
                for(ThrowingConsumer<T> consumer : fn){
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
}
