package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.tree.ConfigTree;
import me.zeroeightsix.fiber.tree.ConfigValue;
import me.zeroeightsix.fiber.tree.Property;
import me.zeroeightsix.fiber.tree.TreeItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeOperationsTest {

    @Test
    @DisplayName("Node -> Node")
    void mergeTo() {
        ConfigTree treeOne = new ConfigNodeBuilder()
                .beginValue("A", Integer.class)
                .withDefaultValue(10)
                .finishValue()
                .build();

        ConfigNodeBuilder nodeTwo = new ConfigNodeBuilder();

        NodeOperations.mergeTo(treeOne, nodeTwo);

        testNodeFor(nodeTwo, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Value -> Node")
    void mergeTo1() {
        ConfigNodeBuilder node = new ConfigNodeBuilder();
        ConfigValue<Integer> value = node.beginValue("A", Integer.class)
                .withDefaultValue(10)
                .build();

        NodeOperations.mergeTo(value, node);

        testNodeFor(node, "A", Integer.class, 10);
    }

    @Test
    @DisplayName("Value -> Value")
    void mergeTo2() {
        ConfigValue<Integer> valueOne = new ConfigValueBuilder<>(null, "A", Integer.class)
                .withDefaultValue(10)
                .build();
        ConfigValue<Integer> valueTwo = new ConfigValueBuilder<>(null, "A", Integer.class)
                .withDefaultValue(20)
                .build();

        NodeOperations.mergeTo(valueOne, valueTwo);
        testItemFor(Integer.class, 10, valueTwo);
    }

    static <T> void testNodeFor(ConfigTree node, String name, Class<T> type, T value) {
        TreeItem item = node.lookup(name);
        testItemFor(type, value, item);
    }

    static <T> void testItemFor(Class<T> type, T value, TreeItem item) {
        assertNotNull(item, "Setting exists");
        assertTrue(item instanceof Property<?>, "Setting is a property");
        Property<?> property = (Property<?>) item;
        assertEquals(type, property.getType(), "Setting type is correct");
        assertEquals(value, ((Property<?>) item).getValue(), "Setting value is correct");
    }
}
