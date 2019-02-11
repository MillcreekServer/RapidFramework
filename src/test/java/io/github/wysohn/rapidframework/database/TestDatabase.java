package io.github.wysohn.rapidframework.database;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.github.wysohn.rapidframework.pluginbase.objects.SimpleChunkLocation;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class TestDatabase {
	Database<DummyObject> db;
	
	@Before
	public void init() {
		db = new Database<DummyObject>(DummyObject.class) {

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
				
			}};
	}

	@Test
	public void testSerialize() {
		DummyObject obj = new DummyObject();
		String serialized = db.serialize(obj);
		assertEquals("{\n" + 
				"  \"nullStr\": null,\n" + 
				"  \"str\": \"test\",\n" + 
				"  \"testInt\": -1,\n" + 
				"  \"testLong\": -2,\n" + 
				"  \"testDouble\": -3.0,\n" + 
				"  \"testBool\": true\n" + 
				"}", serialized);
	}
	
	@Test
	public void testDeserialize() {
		String value = "{\n" + 
				"  \"nullStr\": \"\",\n" + 
				"  \"str\": \"test2\",\n" + 
				"  \"testInt\": -4,\n" + 
				"  \"testLong\": -5,\n" + 
				"  \"testDouble\": -6.0,\n" + 
				"  \"testBool\": true\n" + 
				"}";
		
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
		String value = "{\n" + 
				"  \"nullStr\": null,\n" + 
				"  \"str\": \"test2\",\n" + 
				"  \"testInt\": null,\n" + 
				"  \"testLong\": null,\n" + 
				"  \"testDouble\": null,\n" + 
				"  \"testBool\": null\n" + 
				"}";
		
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
		
		assertEquals("{\n" + 
				"  \"world\": \"testWorld\",\n" + 
				"  \"x\": 0.0,\n" + 
				"  \"y\": 1.0,\n" + 
				"  \"z\": 2.0,\n" + 
				"  \"pitch\": 0.2,\n" + 
				"  \"yaw\": 0.1\n" + 
				"}", serialized);
	}
	
	@Test
	public void testDeserializeLocation() {
		World mockWorld = mock(World.class);
		when(mockWorld.getName()).thenReturn("testWorld2");
		
		PowerMockito.mockStatic(Bukkit.class);
		when(Bukkit.getWorld(anyString())).thenReturn(mockWorld);
		
		String value = "{\n" + 
				"  \"world\": \"testWorld2\",\n" + 
				"  \"x\": 3,\n" + 
				"  \"y\": 4,\n" + 
				"  \"z\": 5,\n" + 
				"  \"yaw\": null\n" + 
				"}";
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
		
		//FileConfiguration seems to be having interesting internal works
		//will work on it later or test it manually
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
		assertEquals("{\n" + 
				"  \"world\": \"testWorld\",\n" + 
				"  \"x\": 1,\n" + 
				"  \"y\": 2,\n" + 
				"  \"z\": 3,\n" + 
				"  \"pitch\": 0.0,\n" + 
				"  \"yaw\": 0.0\n" + 
				"}", serialized);
	}
	
	@Test
	public void testDeserializeSimpleLocation() {
		String value = "{\n" + 
				"  \"world\": \"testWorld\",\n" + 
				"  \"x\": 1,\n" + 
				"  \"y\": 2,\n" + 
				"  \"z\": 3,\n" + 
				"  \"pitch\": 0.0,\n" + 
				"  \"yaw\": 0.0\n" + 
				"}";
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
		assertEquals("{\n" + 
				"  \"world\": \"testWorld\",\n" + 
				"  \"i\": 0,\n" + 
				"  \"j\": 1\n" + 
				"}", serialized);
	}
	
	@Test
	public void testDeserializeSimpleChunkLocation() {
		String value = "{\n" + 
				"  \"world\": \"testWorld\",\n" + 
				"  \"i\": 0,\n" + 
				"  \"j\": 1\n" + 
				"}";
		SimpleChunkLocation scloc = db.deserialize(value, SimpleChunkLocation.class);
		assertEquals("testWorld", scloc.getWorld());
		assertEquals(0, scloc.getI());
		assertEquals(1, scloc.getJ());
	}
	
	public static class DummyObject{
		private String nullStr = null;
		private String str = "test";
		private int testInt = -1;
		private long testLong = -2L;
		private double testDouble = -3.0;
		private boolean testBool = true;
	}
}