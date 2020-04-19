package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.ConstraintType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public final class EnumConstraint<V> extends ValuedConstraint<Set<V>, V> {
    public EnumConstraint(@Nonnull Set<V> value) {
        super(ConstraintType.ONE_OF, Collections.unmodifiableSet(value));
    }

    @Override
    public TestResult<V> test(V value) {
        if (this.getConstraintValue().contains(value)) {
            return TestResult.successful(value);
        }
        return TestResult.unrecoverable();
    }
}
