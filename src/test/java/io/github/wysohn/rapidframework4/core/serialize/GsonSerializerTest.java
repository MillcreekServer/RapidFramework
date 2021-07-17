package io.github.wysohn.rapidframework4.core.serialize;

import com.google.inject.Inject;
import io.github.wysohn.rapidframework4.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework4.data.SimpleLocation;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class GsonSerializerTest {
    @Test
    public void testSerialize() {
        GsonSerializer serializer = new GsonSerializer();

        UUID uuid = UUID.randomUUID();
        DummyObject obj = new DummyObject();
        String serialized = serializer.serializeToString(DummyObject.class, obj);
        assertEquals("{\"stringKey\":\"keynotset\",\"nullStr\":null,\"testStr\":\"notset\",\"testInt\":-1,\"testLong\":-2,\"testDouble\":-3.0,\"testBool\":true}", serialized);
    }

    @Test
    public void testDeserialize() throws Exception {
        GsonSerializer serializer = new GsonSerializer();

        String value = "{" + "\"nullStr\":\"\"," + "\"testStr\":\"test2\"," + "\"testInt\":-4," + "\"testLong\":-5,"
                + "\"testDouble\":-6.0," + "\"testBool\":true," + "\"stringKey\":somekey" + "}";

        DummyObject deserialized = serializer.deserializeFromString(DummyObject.class, value);
        assertEquals("somekey", deserialized.getStringKey());
        assertEquals("", deserialized.nullStr);
        assertEquals("test2", deserialized.testStr);
        assertEquals(-4, deserialized.testInt);
        assertEquals(-5L, deserialized.testLong);
        assertEquals(-6.0, deserialized.testDouble, 0.000001);
        assertTrue(deserialized.testBool);
    }

    @Test
    public void testDeserializeNull() throws Exception {
        GsonSerializer serializer = new GsonSerializer();

        String value = "{" + "\"nullStr\":null," + "\"testStr\":\"test3\"," + "\"testInt\":null," + "\"testLong\":null,"
                + "\"testDouble\":null," + "\"testBool\":null" + "}";

        DummyObject deserialized = serializer.deserializeFromString(DummyObject.class, value);
        assertEquals("", deserialized.nullStr);
        assertEquals("test3", deserialized.testStr);
        assertEquals(0, deserialized.testInt);
        assertEquals(0, deserialized.testLong);
        assertEquals(0.0, deserialized.testDouble, 0.000001);
        assertFalse(deserialized.testBool);
    }

//    @Test
//    public void testSerializeItemStack() {
//        ItemStack IS = new ItemStack(Material.STONE, 5);
//
//        // FileConfiguration seems to be having interesting internal works
//        // will work on it later or test it manually
//        String serialized = db.serialize(IS, ItemStack.class);
//    }
//
//    @Test
//    public void testDeserializeItemStack() {
//
//    }

    @Test
    public void testSerializeUUID() {
        GsonSerializer serializer = new GsonSerializer();

        UUID uuid = UUID.fromString("4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7");
        UUIDInObject obj = new UUIDInObject(uuid);
        String ser = serializer.serializeToString(UUIDInObject.class, obj);
        assertEquals("{\"uuid\":\"4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7\"}", ser);
    }

    @Test
    public void testDeserializeUUID() throws Exception {
        GsonSerializer serializer = new GsonSerializer();

        UUID uuid = serializer.deserializeFromString(UUIDInObject.class, "{\"uuid\":\"4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7\"}").uuid;
        assertEquals(UUID.fromString("4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7"), uuid);
    }

    @Test
    public void testSerializeSimpleLocation() {
        GsonSerializer serializer = new GsonSerializer();

        SimpleLocation sloc = new SimpleLocation("testWorld", 1, 2, 3);
        SimpleLocationInObject obj = new SimpleLocationInObject(sloc);

        String serialized = serializer.serializeToString(SimpleLocationInObject.class, obj);
        assertEquals("{\"simpleLocation\":{\"world\":\"testWorld\",\"x\":1,\"y\":2,\"z\":3,\"pitch\":0.0,\"yaw\":0.0}}", serialized);
    }

    @Test
    public void testDeserializeSimpleLocation() throws Exception {
        GsonSerializer serializer = new GsonSerializer();

        String value = "{\"simpleLocation\":{\"world\":\"testWorld\",\"x\":1,\"y\":2,\"z\":3,\"pitch\":0.0,\"yaw\":0.0}}";
        SimpleLocation sloc = serializer.deserializeFromString(SimpleLocationInObject.class, value).simpleLocation;

        assertEquals("testWorld", sloc.getWorld());
        assertEquals(1, sloc.getX());
        assertEquals(2, sloc.getY());
        assertEquals(3, sloc.getZ());
        assertEquals(0.0f, sloc.getPitch(), 0.00001);
        assertEquals(0.0f, sloc.getYaw(), 0.00001);
    }

    @Test
    public void testSerializeSimpleChunkLocation() {
        GsonSerializer serializer = new GsonSerializer();

        SimpleChunkLocation scloc = new SimpleChunkLocation("testWorld", 2, 3);
        SimpleChunkLocationInObject obj = new SimpleChunkLocationInObject(scloc);

        String serialized = serializer.serializeToString(SimpleChunkLocationInObject.class, obj);
        assertEquals("{\"simpleLocation\":{\"world\":\"testWorld\",\"i\":2,\"j\":3}}", serialized);
    }

    @Test
    public void testDeserializeSimpleChunkLocation() throws Exception {
        GsonSerializer serializer = new GsonSerializer();

        String value = "{\"simpleLocation\":{\"world\":\"testWorld\",\"i\":3,\"j\":4}}";
        SimpleChunkLocation scloc = serializer.deserializeFromString(SimpleChunkLocationInObject.class, value).simpleLocation;
        assertEquals("testWorld", scloc.getWorld());
        assertEquals(3, scloc.getI());
        assertEquals(4, scloc.getJ());
    }

    @Test
    public void testParentTransient() throws Exception {
        GsonSerializer serializer = new GsonSerializer();

        String value = "{" + "\"nullStr\":null," + "\"testStr\":\"test2\"," + "\"testInt\":null," + "\"testLong\":null,"
                + "\"testDouble\":null," + "\"testBool\":null" + "}";

        DummyObject deserialized = serializer.deserializeFromString(DummyObject.class, value);
        System.out.println(deserialized.testStr);
        assertEquals("", deserialized.nullStr);
        assertEquals("test2", deserialized.testStr);
        assertEquals(0, deserialized.testInt);
        assertEquals(0, deserialized.testLong);
        assertEquals(0.0, deserialized.testDouble, 0.000001);
        assertFalse(deserialized.testBool);
    }

    public static class Parent {
        static String s = "static";
        transient String t = "transient";
        @Inject
        String i = "inject";
    }

    public static class DummyObject extends Parent {
        private String stringKey = "keynotset";
        private String nullStr = null;
        private String testStr = "notset";
        private int testInt = -1;
        private long testLong = -2L;
        private double testDouble = -3.0;
        private boolean testBool = true;

        @Inject
        private String shouldIgnore = "ignore";
        @javax.inject.Inject
        private String shouldIgnore2 = "ignore2";
        @Inject
        private static String shouldIgnore3 = "ignore3";
        @Inject
        private transient String shouldIgnore4 = "ignore4";

        public String getStringKey() {
            return stringKey;
        }
    }

    public static class DummyObject2 {
        private String stringKey = "keynotset2";
        private String nullStr = null;
        private String testStr = "notset2";
        private int testInt = -1;
        private long testLong = -2L;
        private double testDouble = -3.0;
        private boolean testBool = true;

        public DummyObject2(String stringKey) {
            this.stringKey = stringKey;
        }
    }

    public static class UUIDInObject {
        private UUID uuid;

        private UUIDInObject() {

        }

        public UUIDInObject(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public static class SimpleLocationInObject {
        private SimpleLocation simpleLocation;

        private SimpleLocationInObject() {
        }

        public SimpleLocationInObject(SimpleLocation simpleLocation) {
            this.simpleLocation = simpleLocation;
        }

    }

    public static class SimpleChunkLocationInObject {
        private SimpleChunkLocation simpleLocation;

        private SimpleChunkLocationInObject() {

        }

        public SimpleChunkLocationInObject(SimpleChunkLocation simpleLocation) {
            this.simpleLocation = simpleLocation;
        }
    }
}