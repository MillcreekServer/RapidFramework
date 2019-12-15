package io.github.wysohn.rapidframework2.core.manager.config;

import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ManagerConfigTest {

    AbstractFileSession mockFileSession;
    ManagerConfig managerConfig;

    @Before
    public void init() {
        mockFileSession = Mockito.mock(AbstractFileSession.class);
        managerConfig = new ManagerConfig(0, mockFileSession);
    }

    @Test
    public void enable() {
    }

    @Test
    public void load() {
    }

    @Test
    public void disable() {
    }

    @Test
    public void get() {
        managerConfig.get("testKey");
        Mockito.verify(mockFileSession).get(Mockito.eq("testKey"));
    }

    @Test
    public void put() {
        managerConfig.put("testKey2", 24253);
        Mockito.verify(mockFileSession).put(Mockito.eq("testKey2"), Mockito.eq(24253));
    }

    @Test
    public void getKeys() {

    }

    @Test
    public void isSection() {
    }
}