package io.github.fablabsmc.fablabs.api.fiber.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigLeafBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigNode;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NodeOperationsTest {
	@Test
	@DisplayName("Node -> Node")
	void moveChildren() {
		ConfigTree treeOne = ConfigTree.builder()
				.withValue("A", ConfigTypes.INTEGER, 10)
				.build();

		ConfigTreeBuilder nodeTwo = ConfigTree.builder();

		NodeOperations.moveChildren(treeOne, nodeTwo);

		testNodeFor(nodeTwo, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN);
	}

	@Test
	@DisplayName("Value -> Node")
	void moveNode() {
		ConfigTreeBuilder node = ConfigTree.builder();
		ConfigLeaf<?> value = node.beginValue("A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN).build();

		NodeOperations.moveNode(value, node);

		testNodeFor(node, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN);
	}

	@Test
	@DisplayName("Value -> Value")
	void copyValue() {
		ConfigLeaf<BigDecimal> valueOne = ConfigLeafBuilder
				.create(null, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN)
				.build();
		ConfigLeaf<BigDecimal> valueTwo = ConfigLeafBuilder
				.create(null, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.valueOf(20))
				.build();

		NodeOperations.copyValue(valueOne, valueTwo);
		testItemFor(ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN, valueTwo);
	}

	public static <T> void testNodeFor(ConfigTree node, String name, SerializableType<T> type, T value) {
		ConfigNode item = node.lookup(name);
		testItemFor(type, value, item);
	}

	static <T> void testItemFor(SerializableType<T> type, T value, ConfigNode item) {
		assertNotNull(item, "Setting exists");
		assertTrue(item instanceof ConfigLeaf<?>, "Setting is a property");
		ConfigLeaf<?> property = (ConfigLeaf<?>) item;
		assertEquals(type, property.getConfigType(), "Setting type is correct");
		assertEquals(value, property.getValue(), "Setting value is correct");
	}
}
