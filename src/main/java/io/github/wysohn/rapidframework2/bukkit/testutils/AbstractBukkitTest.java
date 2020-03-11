package io.github.wysohn.rapidframework2.bukkit.testutils;

import io.github.wysohn.rapidframework2.bukkit.main.objects.BukkitPlayer;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class})
public class AbstractBukkitTest {
    protected static final InetAddress INET_ADDR = InetAddress.getLoopbackAddress();
    protected static final Map<UUID, Player> UUID_PLAYER_MAP = new HashMap<>();
    protected static final Map<String, Player> NAME_PLAYER_MAP = new HashMap<>();

    private Player player;
    protected final UUID PLAYER_UUID = UUID.randomUUID();
    protected final String PLAYER_NAME = "user";

    @Before
    public void setupBukkit(){
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(player.getDisplayName()).thenReturn(PLAYER_NAME);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(player.getLocale()).thenReturn(Locale.ENGLISH.getDisplayName());

        UUID_PLAYER_MAP.put(PLAYER_UUID, player);
        NAME_PLAYER_MAP.put(PLAYER_NAME, player);

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getOfflinePlayer(any(UUID.class))).then(ans -> UUID_PLAYER_MAP.get(ans.getArguments()[0]));
        Mockito.when(Bukkit.getPlayer(anyString())).then(ans -> NAME_PLAYER_MAP.get(ans.getArguments()[0]));
        Mockito.when(Bukkit.getPlayer(any(UUID.class))).then(ans -> UUID_PLAYER_MAP.get(ans.getArguments()[0]));
    }

    /**
     * Returns atomic Player instance
     * @return
     */
    protected Player player(){
        return player;
    }

    protected Player player(UUID uuid, String name) {
        Player p = mock(Player.class);
        when(p.getUniqueId()).thenReturn(uuid);
        when(p.getName()).thenReturn(name);
        when(p.getDisplayName()).thenReturn(name);
        when(p.hasPermission(anyString())).thenReturn(true);
        when(p.getLocale()).thenReturn(Locale.ENGLISH.getDisplayName());

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
