package me.zeroeightsix.fiber.constraint;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class UniqueConstraint<T> extends Constraint<List<T>> {
    private static final UniqueConstraint<?> INSTANCE = new UniqueConstraint<>();

    public static <T> UniqueConstraint<T> instance() {
        @SuppressWarnings("unchecked") UniqueConstraint<T> instance = (UniqueConstraint<T>) INSTANCE;
        return instance;
    }

    private UniqueConstraint() {
        super(ConstraintType.UNIQUE);
    }

    @Override
    public TestResult<List<T>> test(List<T> value) {
        List<T> deduplicated = new ArrayList<>(new LinkedHashSet<>(value));
        return new TestResult<>(value.equals(deduplicated), deduplicated);
    }

    @Override
    public int hashCode() {
        return this.getType().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
