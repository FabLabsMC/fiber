package me.zeroeightsix.fiber.api;

import me.zeroeightsix.fiber.api.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.api.schema.ConfigType;
import me.zeroeightsix.fiber.api.schema.ConfigTypes;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;
import me.zeroeightsix.fiber.api.tree.ConfigTree;
import me.zeroeightsix.fiber.api.tree.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeOperationsTest {

    @Test
    @DisplayName("Node -> Node")
    void moveChildren() {
        ConfigTree treeOne = ConfigTree.builder()
                .withValue("A", ConfigTypes.INTEGER, 10)
                .build();

        ConfigTreeBuilder nodeTwo = ConfigTree.builder();

        NodeOperations.moveChildren(treeOne, nodeTwo);

        testNodeFor(nodeTwo, "A", ConfigTypes.INTEGER, 10);
    }

    @Test
    @DisplayName("Value -> Node")
    void moveNode() {
        ConfigTreeBuilder node = ConfigTree.builder();
        ConfigLeaf<Integer, ?> value = node.beginValue("A", ConfigTypes.INTEGER, 10).build();

        NodeOperations.moveNode(value, node);

        testNodeFor(node, "A", ConfigTypes.INTEGER, 10);
    }

    @Test
    @DisplayName("Value -> Value")
    void copyValue() {
        ConfigLeaf<Integer, ?> valueOne = new ConfigLeafBuilder<>(null, "A", ConfigTypes.INTEGER)
                .withDefaultValue(10)
                .build();
        ConfigLeaf<Integer, ?> valueTwo = new ConfigLeafBuilder<>(null, "A", ConfigTypes.INTEGER)
                .withDefaultValue(20)
                .build();

        NodeOperations.copyValue(valueOne, valueTwo);
        testItemFor(ConfigTypes.INTEGER, 10, valueTwo);
    }

   public static <T> void testNodeFor(ConfigTree node, String name, ConfigType<T, ?> type, T value) {
        ConfigNode item = node.lookup(name);
        testItemFor(type, value, item);
    }

    static <T> void testItemFor(ConfigType<T, ?> type, T value, ConfigNode item) {
        assertNotNull(item, "Setting exists");
        assertTrue(item instanceof Property<?>, "Setting is a property");
        Property<?> property = (Property<?>) item;
        assertEquals(type, property.getConfigType(), "Setting type is correct");
        assertEquals(value, ((Property<?>) item).getValue(), "Setting value is correct");
    }
}
