package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.exceptions.FiberException;
import me.zeroeightsix.fiber.tree.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.reflect.generics.tree.Tree;

import static org.junit.jupiter.api.Assertions.*;

class NodeOperationsTest {

    @Test
    @DisplayName("Node -> Node")
    void mergeTo() {
        Node nodeOne = new ConfigNode();
        Node nodeTwo = new ConfigNode();

        ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(10)
                .withParent(nodeOne)
                .build();

        NodeOperations.mergeTo(nodeOne, nodeTwo);

        testNodeFor(nodeTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Value -> Node")
    void mergeTo1() {
        ConfigNode node = new ConfigNode();
        ConfigValue<Integer> value = ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(10)
                .withParent(node)
                .build();

        NodeOperations.mergeTo(value, node);

        testNodeFor(node, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Value -> Value")
    void mergeTo2() {
        ConfigValue<Integer> valueOne = ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(10)
                .build();

        ConfigValue<Integer> valueTwo = ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(20)
                .build();

        NodeOperations.mergeTo(valueOne, valueTwo);
        testItemFor(Integer.class, 10, valueTwo);
    }

    private <T> void testNodeFor(Node node, String name, Class<T> type, T value) {
        TreeItem item = node.lookup(name);
        testItemFor(type, value, item);
    }

    private <T> void testItemFor(Class<T> type, T value, TreeItem item) {
        assertTrue(item != null, "Setting exists");
        assertTrue(item instanceof Property, "Setting is a property");
        Property property = (Property) item;
        assertEquals(type, property.getType(), "Setting type is correct");
        assertEquals(value, ((Property) item).getValue(), "Setting value is correct");
    }
}