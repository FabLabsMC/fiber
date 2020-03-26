package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.serialization.JanksonSerializer;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.ConfigValue;
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
        ConfigNodeBuilder nodeOne = new ConfigNodeBuilder();
        ConfigNodeBuilder nodeTwo = new ConfigNodeBuilder();

        ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(10)
                .withParent(nodeOne)
                .build();

        ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(20)
                .withParent(nodeTwo)
                .build();

        jk.serialize(nodeOne.build(), bos);
        jk.deserialize(nodeTwo.build(), new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(nodeTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("SubNode -> SubNode")
    void nodeSerialization1() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigNodeBuilder builderOne = new ConfigNodeBuilder();
        ConfigNodeBuilder builderTwo = new ConfigNodeBuilder();
        ConfigNodeBuilder childBuilderOne = builderOne.fork("child");
        ConfigNodeBuilder childBuilderTwo = builderTwo.fork("child");

        ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(10)
                .withParent(childBuilderOne)
                .build();

        ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(20)
                .withParent(childBuilderTwo)
                .build();

        childBuilderOne.build();
        ConfigNode childTwo = childBuilderTwo.build();

        jk.serialize(builderOne.build(), bos);
        jk.deserialize(builderTwo.build(), new ByteArrayInputStream(bos.toByteArray()));
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Ignore SubNode")
    void nodeSerialization2() throws IOException, FiberException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JanksonSerializer jk = new JanksonSerializer();
        ConfigNodeBuilder parentOne = new ConfigNodeBuilder();
        ConfigNodeBuilder parentTwo = new ConfigNodeBuilder();
        ConfigNodeBuilder.Forked<?> childBuilderOne = parentOne.fork("child").serializeSeparately();
        ConfigNodeBuilder.Forked<?> childBuilderTwo = parentTwo.fork("child").serializeSeparately();

        ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(10)
                .withParent(childBuilderOne)
                .build();

        ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(20)
                .withParent(childBuilderTwo)
                .build();

        childBuilderOne.build();
        ConfigNode childTwo = childBuilderTwo.build();

        jk.serialize(parentOne.build(), bos);
        jk.deserialize(parentTwo.build(), new ByteArrayInputStream(bos.toByteArray()));
        // the child data should not have been saved -> default value
        NodeOperationsTest.testNodeFor(childTwo, "A", Integer.class, 20);
    }

}