package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.api.constraint.CompositeType;
import me.zeroeightsix.fiber.api.exception.FiberException;
import me.zeroeightsix.fiber.serialization.JanksonSerializer;
import me.zeroeightsix.fiber.tree.ConfigGroupImpl;
import me.zeroeightsix.fiber.tree.PropertyMirror;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JanksonSerializerTest {

    @Test
    @DisplayName("Node -> Node")
    void nodeSerialization() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigGroupImpl nodeOne = new ConfigTreeBuilder()
                .beginValue("A", Integer.class)
                .withDefaultValue(10)
                .finishValue()
                .build();

        ConfigGroupImpl nodeTwo = new ConfigTreeBuilder()
                .beginValue("A", Integer.class)
                .withDefaultValue(20)
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
        ConfigGroupImpl nodeOne = new ConfigTreeBuilder()
                .fork("child")
                    .beginValue("A", 10)
                    .finishValue()
                .finishNode()
                .build();

        ConfigTreeBuilder builderTwo = new ConfigTreeBuilder();
        ConfigGroupImpl childTwo = builderTwo
                .fork("child")
                    .beginValue("A", 20)
                    .finishValue()
                .build();
        ConfigGroupImpl nodeTwo = builderTwo.build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Constraints")
    void nodeSerializationConstrains() throws IOException, FiberException {
        PropertyMirror<String> versionOne = new PropertyMirror<>();
        PropertyMirror<Integer> settingOne = new PropertyMirror<>();

        ConfigGroupImpl nodeOne = new ConfigTreeBuilder()
                .beginValue("version", "0.1")
                    .withFinality()
                .finishValue(versionOne::mirror)
                .fork("child")
                    .beginValue("A", 10)
                    .finishValue(settingOne::mirror)
                .finishNode()
                .build();

        PropertyMirror<String> versionTwo = new PropertyMirror<>();
        PropertyMirror<Integer> settingTwo = new PropertyMirror<>();

        ConfigGroupImpl nodeTwo = new ConfigTreeBuilder()
                .beginValue("version", "1.0.0")
                .withFinality()
                .beginConstraints() // technically redundant with final, but checks the default value
                    .regex("\\d+\\.\\d+\\.\\d+")
                .finishConstraints()
                .finishValue(versionTwo::mirror)
                .fork("child")
                .beginValue("A", 20)
                    .beginConstraints()
                    .composite(CompositeType.OR)
                        .atMost(0)
                        .atLeast(20)
                    .finishComposite()
                    .finishConstraints()
                .finishValue(settingTwo::mirror)
                .finishNode()
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
        ConfigGroupImpl parentOne = new ConfigTreeBuilder()
                .fork("child").withSeparateSerialization()
                .beginValue("A", 10)
                .finishValue()
                .build();
        ConfigTreeBuilder builderTwo = new ConfigTreeBuilder();
        ConfigGroupImpl childTwo = builderTwo.fork("child").withSeparateSerialization()
                .beginValue("A", 20)
                .finishValue()
                .build();
        ConfigGroupImpl parentTwo = builderTwo.build();

        jk.serialize(parentOne, bos);
        jk.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()));
        // the child data should not have been saved -> default value
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 20);
    }

}