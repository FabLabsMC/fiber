package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigAggregateBuilder;
import me.zeroeightsix.fiber.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.ConfigLeaf;
import me.zeroeightsix.fiber.tree.ConfigTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintsBuilderTest {

    @DisplayName("Test numerical constraints")
    @Test
    public void testNumericalConstraints() {
        List<Constraint<? super Integer>> constraintList = new ArrayList<>();
        ConstraintsBuilder<Integer> constraintsBuilder = new ConstraintsBuilder<>(null, constraintList, Integer.class);
        constraintsBuilder.atLeast(5)
                .composite(CompositeType.OR)
                .atLeast(20)
                .atMost(10)
                .finishComposite()
                .finishConstraints();

        Predicate<Integer> finalConstraint = integer -> constraintList.stream().allMatch(constraint -> constraint.test(integer));

        assertEquals(2, constraintList.size(), "Correct amount of constraints");

        assertFalse(finalConstraint.test(-2), "Input can't be lower than 5");
        assertFalse(finalConstraint.test(4), "Input can't be lower than 5");

        assertFalse(finalConstraint.test(15), "Input can't be between 10 and 20");

        assertTrue(finalConstraint.test(7), "Input can be between 5 and 10");
        assertTrue(finalConstraint.test(25), "Input can be above 20");
    }

    @DisplayName("Test array aggregate constraints")
    @Test
    public void testArrayConstraints() {
        ConfigLeaf<Integer[]> config = ConfigAggregateBuilder.create(null, "foo", Integer[].class)
                .beginConstraints().component()
                .range(3, 10)
                .finishComponent()
                .maxLength(3)
                .finishConstraints()
                .build();

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
        ConfigAggregateBuilder<List<Integer>, Integer> aggregateBuilder = builder.beginAggregateValue("foo", Collections.emptyList(), Integer.class);
        assertThrows(RuntimeFiberException.class, () -> aggregateBuilder.beginConstraints().component().regex(""), "Invalid constraint type at build time");

        ConfigLeaf<List<Integer>> config = aggregateBuilder
                .beginConstraints().component()
                .atLeast(3).atMost(10)
                .finishComponent()
                .maxLength(3)
                .finishConstraints()
                .build();

        ConfigLeaf<List<Integer>> deferredConfig = builder.beginAggregateValue("deferred", Collections.<Integer>emptyList(), null)
                .beginConstraints().component().regex("").finishComponent()
                .finishConstraints().build();
        assertThrows(RuntimeException.class, () -> deferredConfig.setValue(Collections.singletonList(1)),
                "Invalid constraint type (deferred check)"
        );

        assertTrue(config.setValue(Collections.emptyList()));
        assertTrue(config.setValue(Arrays.asList(4, 5, 6)));
        assertFalse(config.setValue(Arrays.asList(1, 2)));
        assertFalse(config.setValue(Arrays.asList(5, 6, 7, 8)));
        assertFalse(config.setValue(Arrays.asList(9, 10, 11)));
    }
}