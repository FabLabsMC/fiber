package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.schema.ConfigTypes;
import me.zeroeightsix.fiber.schema.DecimalConfigType;
import me.zeroeightsix.fiber.schema.ListConfigType;
import me.zeroeightsix.fiber.tree.ConfigLeaf;
import me.zeroeightsix.fiber.tree.ConfigTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintsTest {

    @DisplayName("Test numerical constraints")
    @Test
    public void testNumericalConstraints() {
        DecimalConfigType<Integer> type = ConfigTypes.INTEGER.withMinimum(5);

        assertEquals(1, type.getConstraints().size(), "Correct amount of constraints");
        ConfigLeaf<Integer, BigDecimal> leaf = new ConfigLeafBuilder<>(null, "", type).build();

        assertFalse(leaf.acceptsRaw(BigDecimal.valueOf(-2)), "Input can't be lower than 5");
        assertFalse(leaf.acceptsRaw(BigDecimal.valueOf(4)), "Input can't be lower than 5");
        assertFalse(leaf.acceptsRaw(BigDecimal.valueOf(15)), "Input can't be between 10 and 20");

        assertTrue(leaf.acceptsRaw(BigDecimal.valueOf(7)), "Input can be between 5 and 10");
        assertTrue(leaf.acceptsRaw(BigDecimal.valueOf(25)), "Input can be above 20");
    }

    @DisplayName("Test array aggregate constraints")
    @Test
    public void testArrayConstraints() {
        ConfigLeaf<Integer[], ?> config = new ConfigLeafBuilder<>(null, "foo",
                ConfigTypes.makeArray(ConfigTypes.INTEGER.withValidRange(3, 10, 1)).withMaxSize(3)).build();

        assertTrue(config.setValue(new Integer[0]));
        assertTrue(config.setValue(new Integer[]{4, 5, 6}));
        assertFalse(config.setValue(new Integer[]{1, 2}));
        assertFalse(config.setValue(new Integer[]{5, 6, 7, 8}));
        assertFalse(config.setValue(new Integer[]{9, 10, 11}));
    }

    @DisplayName("Test collection aggregate constraints")
    @Test
    public void testCollectionConstraints() {
        ConfigTreeBuilder builder = ConfigTree.builder();

        ListConfigType<List<Integer>> type = ConfigTypes.makeList(ConfigTypes.INTEGER.withMinimum(3).withMaximum(10)).withMaxSize(3);
        ConfigLeaf<List<Integer>, ?> config = builder.beginValue(
                "",
                type,
                Collections.singletonList(4)
        ).build();

        assertTrue(config.setValue(Collections.emptyList()));
        assertTrue(config.setValue(Arrays.asList(4, 5, 6)));
        assertFalse(config.setValue(Arrays.asList(1, 2)));
        assertFalse(config.setValue(Arrays.asList(5, 6, 7, 8)));
        assertFalse(config.setValue(Arrays.asList(9, 10, 11)));
    }
}