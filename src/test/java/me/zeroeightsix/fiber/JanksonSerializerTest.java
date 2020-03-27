package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.serialization.JanksonSerializer;
import me.zeroeightsix.fiber.tree.ConfigNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class JanksonSerializerTest {

    @Test
    @DisplayName("Node -> Node")
    void nodeSerialization() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigNode nodeOne = new ConfigNodeBuilder()
                .value(Integer.class)
                .name("A")
                .defaultValue(10)
                .finishValue()
                .build();

        ConfigNode nodeTwo = new ConfigNodeBuilder().value(Integer.class)
                .name("A")
                .defaultValue(20)
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
                .fork("child").value(Integer.class)
                .name("A")
                .defaultValue(10)
                .finishValue()
                .build();

        ConfigNodeBuilder builderTwo = new ConfigNodeBuilder();
        ConfigNode childTwo = builderTwo.fork("child").value(Integer.class)
                .name("A")
                .defaultValue(20)
                .finishValue()
                .build();
        ConfigNode nodeTwo = builderTwo.build();

        jk.serialize(nodeOne, bos);
        jk.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Ignore SubNode")
    void nodeSerialization2() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigNode parentOne = new ConfigNodeBuilder()
                .fork("child").serializeSeparately()
                .value(Integer.class)
                .name("A")
                .defaultValue(10)
                .finishValue()
                .build();
        ConfigNodeBuilder builderTwo = new ConfigNodeBuilder();
        ConfigNode childTwo = builderTwo.fork("child").serializeSeparately()
                .value(Integer.class)
                .name("A")
                .defaultValue(20)
                .finishValue()
                .build();
        ConfigNode parentTwo = builderTwo.build();

        jk.serialize(parentOne, bos);
        jk.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()));
        // the child data should not have been saved -> default value
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 20);
    }

}