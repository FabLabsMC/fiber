package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.serialization.JanksonSerializer;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.ConfigValue;
import me.zeroeightsix.fiber.tree.Node;
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
        Node nodeOne = new ConfigNode();
        Node nodeTwo = new ConfigNode();

        ConfigValue.builder("A", Integer.class)
                .withDefaultValue(10)
                .withParent(nodeOne)
                .build();

        ConfigValue.builder("A", Integer.class)
                .withDefaultValue(20)
                .withParent(nodeTwo)
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
        Node parentOne = new ConfigNode();
        Node parentTwo = new ConfigNode();
        Node childOne = parentOne.fork("child");
        Node childTwo = parentTwo.fork("child");

        ConfigValue.builder("A", Integer.class)
                .withDefaultValue(10)
                .withParent(childOne)
                .build();

        ConfigValue.builder("A", Integer.class)
                .withDefaultValue(20)
                .withParent(childTwo)
                .build();

        jk.serialize(parentOne, bos);
        jk.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Ignore SubNode")
    void nodeSerialization2() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        Node parentOne = new ConfigNode();
        Node parentTwo = new ConfigNode();
        Node childOne = parentOne.fork("child", true);
        Node childTwo = parentTwo.fork("child", true);

        ConfigValue.builder("A", Integer.class)
                .withDefaultValue(10)
                .withParent(childOne)
                .build();

        ConfigValue.builder("A", Integer.class)
                .withDefaultValue(20)
                .withParent(childTwo)
                .build();

        jk.serialize(parentOne, bos);
        jk.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()));
        // the child data should not have been saved -> default value
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 20);
    }

}