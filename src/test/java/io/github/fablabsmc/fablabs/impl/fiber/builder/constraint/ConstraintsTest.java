package io.github.fablabsmc.fablabs.impl.fiber.builder.constraint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigLeafBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConstraintsTest {
	@DisplayName("Test numerical constraints")
	@Test
	public void testNumericalConstraints() {
		NumberConfigType<Integer> type = ConfigTypes.INTEGER.withMinimum(5);

		ConfigLeaf<BigDecimal> leaf = ConfigLeafBuilder
				.create(null, "", type.getSerializedType(), BigDecimal.valueOf(5))
				.build();

		assertFalse(leaf.accepts(BigDecimal.valueOf(-2)), "Input can't be lower than 5");
		assertFalse(leaf.accepts(BigDecimal.valueOf(4)), "Input can't be lower than 5");

		assertTrue(leaf.accepts(BigDecimal.valueOf(7)), "Input can be between 5 and 10");
		assertTrue(leaf.accepts(BigDecimal.valueOf(25)), "Input can be above 20");
	}

	@DisplayName("Test array aggregate constraints")
	@Test
	public void testArrayConstraints() {
		ListConfigType<Integer[], BigDecimal> type = ConfigTypes.makeArray(
				ConfigTypes.INTEGER.withValidRange(3, 10, 1)
		).withMaxSize(3).withMinSize(1);
		ConfigLeaf<List<BigDecimal>> config = ConfigLeafBuilder
				.create(null, "foo", type, new Integer[] { 3, 10 })
				.build();
		PropertyMirror<Integer[]> mirror = PropertyMirror.create(type);
		mirror.mirror(config);

		assertFalse(mirror.setValue(new Integer[0]), "unrecoverable size issue");
		assertFalse(mirror.accepts(new Integer[0]), "invalid size");
		assertTrue(mirror.setValue(new Integer[] {4, 5, 6}), "valid array");
		assertTrue(mirror.accepts(new Integer[] {4, 5, 6}), "valid array");
		assertTrue(mirror.setValue(new Integer[] {1, 2}), "recoverable elements");
		assertFalse(mirror.accepts(new Integer[] {1, 2}), "invalid elements");
		assertTrue(mirror.setValue(new Integer[] {5, 6, 7, 8}), "recoverable size");
		assertFalse(mirror.accepts(new Integer[] {5, 6, 7, 8}), "invalid size");
		assertTrue(mirror.setValue(new Integer[] {9, 10, 11}), "recoverable elements");
		assertFalse(mirror.accepts(new Integer[] {9, 10, 11}), "invalid elements");
	}

	@DisplayName("Test collection aggregate constraints")
	@Test
	public void testCollectionConstraints() {
		ConfigTreeBuilder builder = ConfigTree.builder();

		ListConfigType<List<Integer>, BigDecimal> type = ConfigTypes.makeList(ConfigTypes.INTEGER.withMinimum(3).withMaximum(10)).withMaxSize(3);
		ConfigLeaf<?> config = builder.beginValue(
				"",
				type,
				Collections.singletonList(4)
		).build();
		PropertyMirror<List<Integer>> mirror = PropertyMirror.create(type);
		mirror.mirror(config);

		assertTrue(mirror.setValue(Collections.emptyList()));
		assertTrue(mirror.accepts(Collections.emptyList()));
		assertTrue(mirror.setValue(Arrays.asList(4, 5, 6)));
		assertTrue(mirror.accepts(Arrays.asList(4, 5, 6)));
		assertTrue(mirror.setValue(Arrays.asList(1, 2)));
		assertFalse(mirror.accepts(Arrays.asList(1, 2)));
		assertTrue(mirror.setValue(Arrays.asList(5, 6, 7, 8)));
		assertFalse(mirror.accepts(Arrays.asList(5, 6, 7, 8)));
		assertTrue(mirror.setValue(Arrays.asList(9, 10, 11)));
		assertFalse(mirror.accepts(Arrays.asList(9, 10, 11)));
	}
}
