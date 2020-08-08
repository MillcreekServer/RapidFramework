package io.github.wysohn.rapidframework2.bukkit.testutils.manager;

import io.github.wysohn.rapidframework2.core.main.PluginMain;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.mockito.internal.util.reflection.Whitebox;
import util.Validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ManagerTestBuilder<M extends PluginMain.Manager, T extends Event> {
    private final M manager;
    private final Class<T> eventClass;

    private int calls = 0;
    private Function<M, T> mockEvent;
    private Function<M, Boolean> expect;
    private final List<Consumer<M>> afters = new LinkedList<>();

    private ManagerTestBuilder(M manager, Class<T> event) {
        this.manager = manager;
        this.eventClass = event;
    }

    public static <M extends PluginMain.Manager, T extends Event> ManagerTestBuilder<M, T> of(PluginMain main,
                                                                                              M manager, Class<T> event) {
        Whitebox.setInternalState(manager, "main", main);
        return new ManagerTestBuilder<>(manager, event);
    }

    public ManagerTestBuilder<M, T> mockEvent(Function<M, T> fn) {
        this.mockEvent = fn;
        return this;
    }

    public ManagerTestBuilder<M, T> expect(Function<M, Boolean> fn){
        this.expect = fn;
        return this;
    }

    public ManagerTestBuilder<M, T> before(Consumer<M> consumer){
        consumer.accept(manager);
        return this;
    }

    public ManagerTestBuilder<M, T> after(Consumer<M> consumer){
        afters.add(consumer);
        return this;
    }

    public int test() throws InvocationTargetException {
        return test(true);
    }

    public int test(boolean expectBool) throws InvocationTargetException {
        Validation.assertNotNull(mockEvent, "You must mock the event " + eventClass);
        Validation.assertNotNull(expect, "No expectation found.");

        Event event = mockEvent.apply(manager);

        for (Method method : manager.getClass().getMethods()) {
            Annotation annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null
                    || method.getParameterCount() != 1
                    || method.getParameterTypes()[0] != eventClass)
                continue;

            try {
                method.invoke(manager, event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                if(expect.apply(manager)){
                    if(expectBool)
                        calls++;
                } else {
                    if(!expectBool)
                        calls++;
                }
            }
        }

        afters.forEach(consumer -> consumer.accept(manager));

        return calls;
    }
}
