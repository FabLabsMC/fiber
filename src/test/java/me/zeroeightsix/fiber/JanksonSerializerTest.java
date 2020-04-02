package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.serialization.JanksonSerializer;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.ConfigValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JanksonSerializerTest {

    @Test
    @DisplayName("Node -> Node")
    void nodeSerialization() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigNode nodeOne = new ConfigNodeBuilder()
                .beginValue("A", Integer.class)
                .withDefaultValue(10)
                .finishValue()
                .build();

        ConfigNode nodeTwo = new ConfigNodeBuilder()
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
        ConfigNode nodeOne = new ConfigNodeBuilder()
                .fork("child")
                    .beginValue("A", 10)
                    .finishValue()
                .finishNode()
                .build();

        ConfigNodeBuilder builderTwo = new ConfigNodeBuilder();
        ConfigNode childTwo = builderTwo
                .fork("child")
                    .beginValue("A", 20)
                    .finishValue()
                .build();
        ConfigNode nodeTwo = builderTwo.build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Constraints")
    void nodeSerializationConstrains() throws IOException, FiberException {
        AtomicReference<ConfigValue<String>> versionOne = new AtomicReference<>();
        AtomicReference<ConfigValue<Integer>> settingOne = new AtomicReference<>();

        ConfigNode nodeOne = new ConfigNodeBuilder()
                .beginValue("version", "0.1")
                    .withFinality()
                .finishValue(versionOne::set)
                .fork("child")
                    .beginValue("A", 10)
                    .finishValue(settingOne::set)
                .finishNode()
                .build();

        AtomicReference<ConfigValue<String>> versionTwo = new AtomicReference<>();
        AtomicReference<ConfigValue<Integer>> settingTwo = new AtomicReference<>();

        ConfigNode nodeTwo = new ConfigNodeBuilder()
                .beginValue("version", "1.0.0")
                .withFinality()
                .beginConstraints() // technically redundant with final, but checks the default value
                    .regex("\\d+\\.\\d+\\.\\d+")
                .finishConstraints()
                .finishValue(versionTwo::set)
                .fork("child")
                .beginValue("A", 20)
                    .beginConstraints()
                    .composite(CompositeType.OR)
                        .atMost(0)
                        .atLeast(20)
                    .finishComposite()
                    .finishConstraints()
                .finishValue(settingTwo::set)
                .finishNode()
                .build();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        assertEquals("1.0.0", versionTwo.get().getValue(), "RegEx and finality constraints bypassed");
        assertEquals(20, settingTwo.get().getValue(), "Range constraint bypassed");

        bos.reset();

        versionOne.get().setValue("0.1.0");
        settingOne.get().setValue(-5);
        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        assertEquals("1.0.0", versionTwo.get().getValue(), "Finality bypassed");
        assertEquals(-5, settingTwo.get().getValue(), "Valid value rejected");
    }

    @Test
    @DisplayName("Ignore SubNode")
    void nodeSerialization2() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigNode parentOne = new ConfigNodeBuilder()
                .fork("child").withSeparateSerialization()
                .beginValue("A", 10)
                .finishValue()
                .build();
        ConfigNodeBuilder builderTwo = new ConfigNodeBuilder();
        ConfigNode childTwo = builderTwo.fork("child").withSeparateSerialization()
                .beginValue("A", 20)
                .finishValue()
                .build();
        ConfigNode parentTwo = builderTwo.build();

        jk.serialize(parentOne, bos);
        jk.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()));
        // the child data should not have been saved -> default value
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 20);
    }

}