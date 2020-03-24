package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.tree.ConfigValue;
import me.zeroeightsix.fiber.tree.NodeLike;
import me.zeroeightsix.fiber.tree.Property;
import me.zeroeightsix.fiber.tree.TreeItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeOperationsTest {

    @Test
    @DisplayName("Node -> Node")
    void mergeTo() {
        ConfigNodeBuilder nodeOne = new ConfigNodeBuilder();
        ConfigNodeBuilder nodeTwo = new ConfigNodeBuilder();

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
        ConfigNodeBuilder node = new ConfigNodeBuilder();
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

    static <T> void testNodeFor(NodeLike node, String name, Class<T> type, T value) {
        TreeItem item = node.lookup(name);
        testItemFor(type, value, item);
    }

    static <T> void testItemFor(Class<T> type, T value, TreeItem item) {
        assertTrue(item != null, "Setting exists");
        assertTrue(item instanceof Property<?>, "Setting is a property");
        Property<?> property = (Property<?>) item;
        assertEquals(type, property.getType(), "Setting type is correct");
        assertEquals(value, ((Property<?>) item).getValue(), "Setting value is correct");
    }
}
