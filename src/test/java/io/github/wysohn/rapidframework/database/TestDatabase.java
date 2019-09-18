package io.github.wysohn.rapidframework.database;

import io.github.wysohn.rapidframework.pluginbase.objects.SimpleChunkLocation;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class TestDatabase {
    Database<DummyObject> db;

    @Before
    public void init() {
        db = new Database<DummyObject>(DummyObject.class, "Dummy") {

            @Override
            public DummyObject load(String key, DummyObject def) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void save(String key, DummyObject value) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean has(String key) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Set<String> getKeys() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void clear() {
                // TODO Auto-generated method stub

            }
        };
    }

    @Test
    public void testSerialize() {
        DummyObject obj = new DummyObject();
        String serialized = db.serialize(obj);
        assertEquals("{" + "\"nullStr\":null," + "\"str\":\"test\"," + "\"testInt\":-1," + "\"testLong\":-2,"
                + "\"testDouble\":-3.0," + "\"testBool\":true" + "}", serialized);
    }

    @Test
    public void testDeserialize() {
        String value = "{" + "\"nullStr\":\"\"," + "\"str\":\"test2\"," + "\"testInt\":-4," + "\"testLong\":-5,"
                + "\"testDouble\":-6.0," + "\"testBool\":true" + "}";

        DummyObject deserialized = db.deserialize(value, DummyObject.class);
        assertEquals("", deserialized.nullStr);
        assertEquals("test2", deserialized.str);
        assertEquals(-4, deserialized.testInt);
        assertEquals(-5L, deserialized.testLong);
        assertEquals(-6.0, deserialized.testDouble, 0.000001);
        assertTrue(deserialized.testBool);
    }

    @Test
    public void testDeserializeNull() {
        String value = "{" + "\"nullStr\":null," + "\"str\":\"test2\"," + "\"testInt\":null," + "\"testLong\":null,"
                + "\"testDouble\":null," + "\"testBool\":null" + "}";

        DummyObject deserialized = db.deserialize(value, DummyObject.class);
        assertEquals("", deserialized.nullStr);
        assertEquals("test2", deserialized.str);
        assertEquals(0, deserialized.testInt);
        assertEquals(0, deserialized.testLong);
        assertEquals(0.0, deserialized.testDouble, 0.000001);
        assertFalse(deserialized.testBool);
    }

    @Test
    public void testSerializeLocation() {
        World mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn("testWorld");

        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getWorld(anyString())).thenReturn(mockWorld);

        Location loc = new Location(mockWorld, 0, 1, 2, 0.1f, 0.2f);

        String serialized = db.serialize(loc);

        assertEquals("{" + "\"world\":\"testWorld\"," + "\"x\":0.0," + "\"y\":1.0," + "\"z\":2.0," + "\"pitch\":0.2,"
                + "\"yaw\":0.1" + "}", serialized);
    }

    @Test
    public void testDeserializeLocation() {
        World mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn("testWorld2");

        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getWorld(anyString())).thenReturn(mockWorld);

        String value = "{" + "\"world\":\"testWorld2\"," + "\"x\":3," + "\"y\":4," + "\"z\":5," + "\"yaw\":null" + "}";
        Location loc = db.deserialize(value, Location.class);
        assertEquals("testWorld2", loc.getWorld().getName());
        assertEquals(3.0, loc.getX(), 0.000001);
        assertEquals(4.0, loc.getY(), 0.000001);
        assertEquals(5.0, loc.getZ(), 0.000001);
        assertEquals(0.0f, loc.getPitch(), 0.000001);
        assertEquals(0.0f, loc.getYaw(), 0.000001);
    }

    @Test
    public void testSerializeItemStack() {
        ItemStack IS = new ItemStack(Material.STONE, 5);

        // FileConfiguration seems to be having interesting internal works
        // will work on it later or test it manually
        String serialized = db.serialize(IS, ItemStack.class);
    }

    @Test
    public void testDeserializeItemStack() {

    }

    @Test
    public void testSerializeUUID() {
        UUID uuid = UUID.fromString("4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7");
        String ser = db.serialize(uuid);
        assertEquals("\"4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7\"", ser);
    }

    @Test
    public void testDeserializeUUID() {
        UUID uuid = db.deserialize("4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7", UUID.class);
        assertEquals(UUID.fromString("4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7"), uuid);
    }

    @Test
    public void testSerializeSimpleLocation() {
        SimpleLocation sloc = new SimpleLocation("testWorld", 1, 2, 3);
        String serialized = db.serialize(sloc);
        assertEquals("{" + "\"world\":\"testWorld\"," + "\"x\":1," + "\"y\":2," + "\"z\":3," + "\"pitch\":0.0,"
                + "\"yaw\":0.0" + "}", serialized);
    }

    @Test
    public void testDeserializeSimpleLocation() {
        String value = "{" + "\"world\":\"testWorld\"," + "\"x\":1," + "\"y\":2," + "\"z\":3," + "\"pitch\":0.0,"
                + "\"yaw\":0.0" + "}";
        SimpleLocation sloc = db.deserialize(value, SimpleLocation.class);
        assertEquals("testWorld", sloc.getWorld());
        assertEquals(1, sloc.getX());
        assertEquals(2, sloc.getY());
        assertEquals(3, sloc.getZ());
        assertEquals(0.0f, sloc.getPitch(), 0.00001);
        assertEquals(0.0f, sloc.getYaw(), 0.00001);
    }

    @Test
    public void testSerializeSimpleChunkLocation() {
        SimpleChunkLocation scloc = new SimpleChunkLocation("testWorld", 0, 1);
        String serialized = db.serialize(scloc);
        assertEquals("{" + "\"world\":\"testWorld\"," + "\"i\":0," + "\"j\":1" + "}", serialized);
    }

    @Test
    public void testDeserializeSimpleChunkLocation() {
        String value = "{" + "\"world\":\"testWorld\"," + "\"i\":0," + "\"j\":1" + "}";
        SimpleChunkLocation scloc = db.deserialize(value, SimpleChunkLocation.class);
        assertEquals("testWorld", scloc.getWorld());
        assertEquals(0, scloc.getI());
        assertEquals(1, scloc.getJ());
    }

    public static class DummyObject {
        private String nullStr = null;
        private String str = "test";
        private int testInt = -1;
        private long testLong = -2L;
        private double testDouble = -3.0;
        private boolean testBool = true;
    }
}
