package me.zeroeightsix.fiber.impl.serialization;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import me.zeroeightsix.fiber.api.NodeOperationsTest;
import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.api.constraint.CompositeType;
import me.zeroeightsix.fiber.api.exception.FiberException;
import me.zeroeightsix.fiber.api.serialization.Marshaller;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigTree;
import me.zeroeightsix.fiber.api.tree.PropertyMirror;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JanksonSerializerTest {

    @Test
    @DisplayName("Node -> Node")
    void nodeSerialization() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree nodeOne = ConfigTree.builder()
                .beginValue("A", Integer.class, null)
                .withDefaultValue(10)
                .finishValue()
                .build();

        ConfigTree nodeTwo = ConfigTree.builder()
                .beginValue("A", Integer.class, 20)
                .finishValue()
                .build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(nodeTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("SubNode -> SubNode")
    void nodeSerialization1() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree nodeOne = ConfigTree.builder()
                .fork("child")
                    .beginValue("A", Integer.class, 10)
                    .finishValue()
                .finishBranch()
                .build();

        ConfigTreeBuilder builderTwo = ConfigTree.builder();
        ConfigTree childTwo = builderTwo
                .fork("child")
                    .beginValue("A", Integer.class, 20)
                    .finishValue()
                .build();
        ConfigTree nodeTwo = builderTwo.build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Constraints")
    void nodeSerializationConstrains() throws IOException, FiberException {
        PropertyMirror<String> versionOne = new PropertyMirror<>();
        PropertyMirror<Integer> settingOne = new PropertyMirror<>();

        ConfigTree nodeOne = ConfigTree.builder()
                .beginValue("version", String.class, "0.1")
                    .withFinality()
                .finishValue(versionOne::mirror)
                .fork("child")
                    .beginValue("A", Integer.class, 10)
                    .finishValue(settingOne::mirror)
                .finishBranch()
                .build();

        PropertyMirror<String> versionTwo = new PropertyMirror<>();
        PropertyMirror<Integer> settingTwo = new PropertyMirror<>();

        ConfigTree nodeTwo = ConfigTree.builder()
                .beginValue("version", String.class, "1.0.0")
                .withFinality()
                .beginConstraints() // technically redundant with final, but checks the default value
                    .regex("\\d+\\.\\d+\\.\\d+")
                .finishConstraints()
                .finishValue(versionTwo::mirror)
                .fork("child")
                .beginValue("A", int.class, 20)
                    .beginConstraints()
                    .composite(CompositeType.OR)
                        .atMost(0)
                        .atLeast(20)
                    .finishComposite()
                    .finishConstraints()
                .finishValue(settingTwo::mirror)
                .finishBranch()
                .build();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        assertEquals("1.0.0", versionTwo.getValue(), "RegEx and finality constraints bypassed");
        assertEquals(20, settingTwo.getValue(), "Range constraint bypassed");

        bos.reset();

        versionOne.setValue("0.1.0");
        settingOne.setValue(-5);
        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        assertEquals("1.0.0", versionTwo.getValue(), "Finality bypassed");
        assertEquals(-5, settingTwo.getValue(), "Valid value rejected");
    }

    @Test
    @DisplayName("Ignore SubNode")
    void nodeSerialization2() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree parentOne = ConfigTree.builder()
                .fork("child").withSeparateSerialization()
                .beginValue("A", int.class, 10)
                .finishValue()
                .build();
        ConfigTreeBuilder builderTwo = ConfigTree.builder();
        ConfigTree childTwo = builderTwo.fork("child").withSeparateSerialization()
                .beginValue("A", Integer.class, 20)
                .finishValue()
                .build();
        ConfigTree parentTwo = builderTwo.build();

        jk.serialize(parentOne, bos);
        jk.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()));
        // the child data should not have been saved -> default value
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 20);
    }

    @Test
    @DisplayName("Extended marshaller")
    void testExtendedMarshaller() throws IOException, FiberException {
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
                            return (A) new SomeObject(
                                    object.getInt("some_a", 0),
                                    object.get(String.class, "some_b")
                            );
                        }
                        return null;
                    }
                }), false
        );

        SomeObject so = new SomeObject(0, "foo");

        ConfigBranch branch = ConfigTree.builder()
                .withValue("some", SomeObject.class, so)
                .build();

        ConfigBranch branch2 = ConfigTree.builder()
                .withValue("some", SomeObject.class, null)
                .build();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        jk.serialize(branch, bos);
        jk.deserialize(branch2, new ByteArrayInputStream(bos.toByteArray()));

        NodeOperationsTest.testNodeFor(branch2, "some", SomeObject.class, so);
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