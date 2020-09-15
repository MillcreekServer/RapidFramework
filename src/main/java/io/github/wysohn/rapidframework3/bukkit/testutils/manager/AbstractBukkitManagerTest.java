package io.github.wysohn.rapidframework3.bukkit.testutils.manager;

import io.github.wysohn.rapidframework3.bukkit.testutils.AbstractBukkitTest;
import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.interfaces.caching.IObserver;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mockito.internal.util.reflection.Whitebox;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.mock;

public class AbstractBukkitManagerTest extends AbstractBukkitTest {
    protected static final String MESSAGE_JOIN = "join message";

    protected void mockEvent(Manager manager, Event event) {
        for (Method method : manager.getClass().getMethods()) {
            Annotation annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null
                    || method.getParameterCount() != 1
                    || method.getParameterTypes()[0] != event.getClass())
                continue;

            try {
                method.invoke(manager, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    protected AsyncPlayerPreLoginEvent login() {
        return new AsyncPlayerPreLoginEvent(PLAYER_NAME, INET_ADDR, PLAYER_UUID);
    }

    protected PlayerJoinEvent join(Player player) {
        return new PlayerJoinEvent(player, MESSAGE_JOIN);
    }

    protected <K, V extends CachedElement<K>> void fakeDB(AbstractManagerElementCaching<K, V> manager,
                                                          Database mockDatabse) {
        Whitebox.setInternalState(manager, "db", mockDatabse);
    }

    protected IObserver addFakeObserver(AbstractManagerElementCaching.ObservableElement element) {
        IObserver observer = mock(IObserver.class);
        List<IObserver> observers = (List<IObserver>) Whitebox.getInternalState(element, "observers");
        observers.add(observer);
        return observer;
    }
}
