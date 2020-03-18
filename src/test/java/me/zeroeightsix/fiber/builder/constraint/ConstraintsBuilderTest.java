package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.tree.ConfigValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintsBuilderTest {

    @DisplayName("Test numerical constraints")
    @Test
    public void testNumericalConstraints() {
        List<Constraint<? super Integer>> constraintList = new ArrayList<>();
        ConstraintsBuilder.Scalar<Void, Integer> constraintsBuilder = ConstraintsBuilder.scalar(null, constraintList, Integer.class);
        constraintsBuilder.biggerThan(5)
                .composite(CompositeType.OR)
                .biggerThan(20)
                .smallerThan(10)
                .finishComposite()
                .finish();

        Predicate<Integer> finalConstraint = integer -> constraintList.stream().allMatch(constraint -> constraint.test(integer));

        assertEquals(2, constraintList.size(), "Correct amount of constraints");

        assertFalse(finalConstraint.test(-2), "Input can't be lower than 5");
        assertFalse(finalConstraint.test(4), "Input can't be lower than 5");

        assertFalse(finalConstraint.test(15), "Input can't be between 10 and 20");

        assertTrue(finalConstraint.test(7), "Input can be between 5 and 10");
        assertTrue(finalConstraint.test(25), "Input can be above 20");
    }

    @DisplayName("Test aggregate constraints")
    @Test
    public void testComponentConstraints() {
        ConfigValue<Integer[]> config = ConfigValueBuilder.aggregate(Integer[].class)
                .constraints().component()
                .biggerThan(3).smallerThan(10)
                .finishComponent()
                .maxLength(3)
                .finish()
                .build();

        assertTrue(config.setValue(new Integer[0]));
        assertTrue(config.setValue(new Integer[]{4, 5, 6}));
        assertFalse(config.setValue(new Integer[]{1, 2}));
        assertFalse(config.setValue(new Integer[]{5, 6, 7, 8}));
        assertFalse(config.setValue(new Integer[]{9, 10, 11}));
    }

}