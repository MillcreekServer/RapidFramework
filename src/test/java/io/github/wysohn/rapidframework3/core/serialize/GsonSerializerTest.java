package io.github.wysohn.rapidframework3.core.serialize;

import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;
import org.junit.Test;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.*;

public class GsonSerializerTest {
    @Test
    public void testSerialize() {
        GsonSerializer<DummyObject> serializer = new GsonSerializer(DummyObject.class);

        UUID uuid = UUID.randomUUID();
        DummyObject obj = new DummyObject();
        String serialized = serializer.serializeToString(obj);
        assertEquals("{\"stringKey\":\"keynotset\",\"nullStr\":null,\"testStr\":\"notset\",\"testInt\":-1,\"testLong\":-2,\"testDouble\":-3.0,\"testBool\":true}", serialized);
    }

    @Test
    public void testDeserialize() {
        GsonSerializer<DummyObject> serializer = new GsonSerializer(DummyObject.class);

        String value = "{" + "\"nullStr\":\"\"," + "\"testStr\":\"test2\"," + "\"testInt\":-4," + "\"testLong\":-5,"
                + "\"testDouble\":-6.0," + "\"testBool\":true," + "\"stringKey\":somekey" + "}";

        DummyObject deserialized = serializer.deserializeFromString(value);
        assertEquals("somekey", deserialized.getStringKey());
        assertEquals("", deserialized.nullStr);
        assertEquals("test2", deserialized.testStr);
        assertEquals(-4, deserialized.testInt);
        assertEquals(-5L, deserialized.testLong);
        assertEquals(-6.0, deserialized.testDouble, 0.000001);
        assertTrue(deserialized.testBool);
    }

    @Test
    public void testDeserializeNull() {
        GsonSerializer<DummyObject> serializer = new GsonSerializer(DummyObject.class);

        String value = "{" + "\"nullStr\":null," + "\"testStr\":\"test3\"," + "\"testInt\":null," + "\"testLong\":null,"
                + "\"testDouble\":null," + "\"testBool\":null" + "}";

        DummyObject deserialized = serializer.deserializeFromString(value);
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
        GsonSerializer<UUIDInObject> serializer = new GsonSerializer(UUIDInObject.class);

        UUID uuid = UUID.fromString("4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7");
        UUIDInObject obj = new UUIDInObject(uuid);
        String ser = serializer.serializeToString(obj);
        assertEquals("{\"uuid\":\"4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7\"}", ser);
    }

    @Test
    public void testDeserializeUUID() {
        GsonSerializer<UUIDInObject> serializer = new GsonSerializer(UUIDInObject.class);

        UUID uuid = serializer.deserializeFromString("{\"uuid\":\"4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7\"}").uuid;
        assertEquals(UUID.fromString("4b472ef8-5ab9-4d5a-9c74-f2fc318d70e7"), uuid);
    }

    @Test
    public void testSerializeSimpleLocation() {
        GsonSerializer<SimpleLocationInObject> serializer = new GsonSerializer(SimpleLocationInObject.class);

        SimpleLocation sloc = new SimpleLocation("testWorld", 1, 2, 3);
        SimpleLocationInObject obj = new SimpleLocationInObject(sloc);

        String serialized = serializer.serializeToString(obj);
        assertEquals("{\"simpleLocation\":{\"world\":\"testWorld\",\"x\":1,\"y\":2,\"z\":3,\"pitch\":0.0,\"yaw\":0.0}}", serialized);
    }

    @Test
    public void testDeserializeSimpleLocation() {
        GsonSerializer<SimpleLocationInObject> serializer = new GsonSerializer(SimpleLocationInObject.class);

        String value = "{\"simpleLocation\":{\"world\":\"testWorld\",\"x\":1,\"y\":2,\"z\":3,\"pitch\":0.0,\"yaw\":0.0}}";
        SimpleLocation sloc = serializer.deserializeFromString(value).simpleLocation;

        assertEquals("testWorld", sloc.getWorld());
        assertEquals(1, sloc.getX());
        assertEquals(2, sloc.getY());
        assertEquals(3, sloc.getZ());
        assertEquals(0.0f, sloc.getPitch(), 0.00001);
        assertEquals(0.0f, sloc.getYaw(), 0.00001);
    }

    @Test
    public void testSerializeSimpleChunkLocation() {
        GsonSerializer<SimpleChunkLocationInObject> serializer = new GsonSerializer(SimpleChunkLocationInObject.class);

        SimpleChunkLocation scloc = new SimpleChunkLocation("testWorld", 2, 3);
        SimpleChunkLocationInObject obj = new SimpleChunkLocationInObject(scloc);

        String serialized = serializer.serializeToString(obj);
        assertEquals("{\"simpleLocation\":{\"world\":\"testWorld\",\"i\":2,\"j\":3}}", serialized);
    }

    @Test
    public void testDeserializeSimpleChunkLocation() {
        GsonSerializer<SimpleChunkLocationInObject> serializer = new GsonSerializer(SimpleChunkLocationInObject.class);

        String value = "{\"simpleLocation\":{\"world\":\"testWorld\",\"i\":3,\"j\":4}}";
        SimpleChunkLocation scloc = serializer.deserializeFromString(value).simpleLocation;
        assertEquals("testWorld", scloc.getWorld());
        assertEquals(3, scloc.getI());
        assertEquals(4, scloc.getJ());
    }

    @Test
    public void testParentTransient() throws Exception {
        GsonSerializer<DummyObject> serializer = new GsonSerializer(DummyObject.class);

        String value = "{" + "\"nullStr\":null," + "\"testStr\":\"test2\"," + "\"testInt\":null," + "\"testLong\":null,"
                + "\"testDouble\":null," + "\"testBool\":null" + "}";

        DummyObject deserialized = serializer.deserializeFromString(value);
        System.out.println(deserialized.testStr);
        assertEquals("", deserialized.nullStr);
        assertEquals("test2", deserialized.testStr);
        assertEquals(0, deserialized.testInt);
        assertEquals(0, deserialized.testLong);
        assertEquals(0.0, deserialized.testDouble, 0.000001);
        assertFalse(deserialized.testBool);
    }

    @Test
    public void testTypeAssertion() {
        new GsonSerializer(DummyObject.class);
    }

    @Test(expected = AssertionError.class)
    public void testTypeAssertionFail() {
        new GsonSerializer(DummyObject2.class);
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