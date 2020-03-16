package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
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
        ConstraintsBuilder<Integer> constraintsBuilder = new ConstraintsBuilder<>(constraintList, Integer.class, null);
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

}