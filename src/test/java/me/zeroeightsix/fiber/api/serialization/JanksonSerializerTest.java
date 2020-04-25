package me.zeroeightsix.fiber.api.serialization;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import me.zeroeightsix.fiber.api.NodeOperationsTest;
import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.api.exception.FiberException;
import me.zeroeightsix.fiber.api.schema.type.derived.ConfigTypes;
import me.zeroeightsix.fiber.api.schema.type.derived.NumberDerivedType;
import me.zeroeightsix.fiber.api.tree.ConfigTree;
import me.zeroeightsix.fiber.api.tree.HasValue;
import me.zeroeightsix.fiber.api.tree.PropertyMirror;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JanksonSerializerTest {

    public static final NumberDerivedType<Integer> INT_TYPE = ConfigTypes.INTEGER.derive(int.class, Function.identity(), Function.identity());

    @Test
    @DisplayName("Node -> Node")
    void nodeSerialization() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree nodeOne = ConfigTree.builder()
                .beginValue("A", ConfigTypes.INTEGER.getSerializedType(), null)
                .withDefaultValue(BigDecimal.TEN)
                .finishValue()
                .build();

        ConfigTree nodeTwo = ConfigTree.builder()
                .withValue("A", ConfigTypes.INTEGER, 20)
                .build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(nodeTwo, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN);
    }

    @Test
    @DisplayName("SubNode -> SubNode")
    void nodeSerialization1() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree nodeOne = ConfigTree.builder()
                .fork("child")
                    .withValue("A", ConfigTypes.INTEGER, 10)
                .finishBranch()
                .build();

        ConfigTreeBuilder builderTwo = ConfigTree.builder();
        ConfigTree childTwo = builderTwo
                .fork("child")
                    .withValue("A", ConfigTypes.INTEGER, 20)
                .build();
        ConfigTree nodeTwo = builderTwo.build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN);
    }

    @Test
    @DisplayName("Constraints")
    void nodeSerializationConstrains() throws IOException, FiberException {
        PropertyMirror<String> versionOne = PropertyMirror.create(ConfigTypes.STRING);
        PropertyMirror<Integer> settingOne = PropertyMirror.create(ConfigTypes.INTEGER);

        ConfigTree nodeOne = ConfigTree.builder()
                .beginValue("version", ConfigTypes.STRING.getSerializedType(), "0.1")
                .finishValue(versionOne::mirror)
                .fork("child")
                    .beginValue("A", ConfigTypes.INTEGER, 30)
                    .finishValue(settingOne::mirror)
                .finishBranch()
                .build();

        PropertyMirror<String> versionTwo = PropertyMirror.create(ConfigTypes.STRING);
        PropertyMirror<Integer> settingTwo = PropertyMirror.create(ConfigTypes.INTEGER);

        ConfigTree nodeTwo = ConfigTree.builder()
                .beginValue("version", ConfigTypes.STRING.withPattern("\\d+\\.\\d+\\.\\d+"), "1.0.0")
                .finishValue(versionTwo::mirror)
                .fork("child")
                .beginValue("A", INT_TYPE.withMinimum(-5).withMaximum(20), 20)
                .finishValue(settingTwo::mirror)
                .finishBranch()
                .build();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        assertEquals("1.0.0", versionTwo.getValue(), "RegEx constraint bypassed");
        assertEquals(20, settingTwo.getValue(), "Range constraint bypassed");

        bos.reset();

        versionOne.setValue("0.1.0");
        settingOne.setValue(-5);
        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        assertEquals("0.1.0", versionTwo.getValue(), "Valid value rejected");
        assertEquals(-5, settingTwo.getValue(), "Valid value rejected");
    }

    @Test
    @DisplayName("Ignore SubNode")
    void nodeSerialization2() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree parentOne = ConfigTree.builder()
                .fork("child").withSeparateSerialization()
                .beginValue("A", INT_TYPE, 10)
                .finishValue()
                .build();
        ConfigTreeBuilder builderTwo = ConfigTree.builder();
        ConfigTree childTwo = builderTwo.fork("child").withSeparateSerialization()
                .beginValue("A", ConfigTypes.INTEGER, 20)
                .finishValue()
                .build();
        ConfigTree parentTwo = builderTwo.build();

        jk.serialize(parentOne, bos);
        jk.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()));
        // the child data should not have been saved -> default value
        NodeOperationsTest.testNodeFor(childTwo, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.valueOf(20));
    }

    @Test
    @DisplayName("Extended marshaller")
    void testExtendedMarshaller() {
        JanksonSerializer jk = new JanksonSerializer(
                JanksonSerializer.extendDefaultMarshaller(new Marshaller<JsonElement>() {
                    @Override
                    public JsonElement marshall(Object value) {
                        if (value instanceof SomeObject) {
                            JsonObject object = new JsonObject();
                            object.put("some_a", new JsonPrimitive(((SomeObject) value).a));
                            object.put("some_b", new JsonPrimitive(((SomeObject) value).b));
                            return object;
                        }
                        return null;
                    }

                    @Override
                    public <A> A marshallReverse(Class<A> type, JsonElement value) {
                        if (type.equals(SomeObject.class)) {
                            JsonObject object = (JsonObject) value;
                            return type.cast(new SomeObject(
                                    object.getInt("some_a", 0),
                                    object.get(String.class, "some_b")
                            ));
                        }
                        return null;
                    }
                }), false
        );

        JsonElement foo = jk.serialize(new HasValue<SomeObject>() {
            final SomeObject so = new SomeObject(0, "foo");

            @Override
            public SomeObject getValue() {
                return this.so;
            }

            @Override
            public Class<SomeObject> getType() {
                return SomeObject.class;
            }
        });
        SomeObject foo2 = jk.marshall(SomeObject.class, foo);

        assertNotNull(foo2);
        assertEquals("foo", foo2.b);
    }

    private class SomeObject {
        private final int a;
        private final String b;

        public SomeObject(int a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SomeObject that = (SomeObject) o;
            return a == that.a &&
                    Objects.equals(b, that.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

}