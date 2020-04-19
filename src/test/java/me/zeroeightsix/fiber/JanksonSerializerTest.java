package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.schema.ConfigTypes;
import me.zeroeightsix.fiber.schema.DecimalConfigType;
import me.zeroeightsix.fiber.serialization.JanksonSerializer;
import me.zeroeightsix.fiber.tree.ConfigTree;
import me.zeroeightsix.fiber.tree.PropertyMirror;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JanksonSerializerTest {

    public static final DecimalConfigType<Integer> INT_TYPE = ConfigTypes.INTEGER.derive(int.class, Function.identity(), Function.identity());

    @Test
    @DisplayName("Node -> Node")
    void nodeSerialization() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree nodeOne = ConfigTree.builder()
                .beginValue("A", ConfigTypes.INTEGER, null)
                .withDefaultValue(10)
                .finishValue()
                .build();

        ConfigTree nodeTwo = ConfigTree.builder()
                .beginValue("A", ConfigTypes.INTEGER, 20)
                .finishValue()
                .build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(nodeTwo, "A", ConfigTypes.INTEGER, 10);
    }

    @Test
    @DisplayName("SubNode -> SubNode")
    void nodeSerialization1() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigTree nodeOne = ConfigTree.builder()
                .fork("child")
                    .beginValue("A", ConfigTypes.INTEGER, 10)
                    .finishValue()
                .finishBranch()
                .build();

        ConfigTreeBuilder builderTwo = ConfigTree.builder();
        ConfigTree childTwo = builderTwo
                .fork("child")
                    .beginValue("A", ConfigTypes.INTEGER, 20)
                    .finishValue()
                .build();
        ConfigTree nodeTwo = builderTwo.build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", ConfigTypes.INTEGER, 10);
    }

    @Test
    @DisplayName("Constraints")
    void nodeSerializationConstrains() throws IOException, FiberException {
        PropertyMirror<String> versionOne = new PropertyMirror<>();
        PropertyMirror<Integer> settingOne = new PropertyMirror<>();

        ConfigTree nodeOne = ConfigTree.builder()
                .beginValue("version", ConfigTypes.STRING, "0.1")
                .finishValue(versionOne::mirror)
                .fork("child")
                    .beginValue("A", ConfigTypes.INTEGER, 30)
                    .finishValue(settingOne::mirror)
                .finishBranch()
                .build();

        PropertyMirror<String> versionTwo = new PropertyMirror<>();
        PropertyMirror<Integer> settingTwo = new PropertyMirror<>();

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
        NodeOperationsTest.testNodeFor(childTwo, "A", ConfigTypes.INTEGER, 20);
    }

}