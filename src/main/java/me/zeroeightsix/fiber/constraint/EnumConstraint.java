package me.zeroeightsix.fiber.constraint;

import javax.annotation.Nonnull;
import java.util.Set;

public class EnumConstraint<V> extends ValuedConstraint<Set<V>, V>  {
    public EnumConstraint(@Nonnull Set<V> value) {
        super(ConstraintType.ONE_OF, value);
    }

    @Override
    public TestResult<V> test(V value) {
        if (this.getConstraintValue().contains(value)) {
            return TestResult.successful(value);
        }
        return TestResult.unrecoverable();
    }
}
