package me.zeroeightsix.fiber.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A component constraints is satisfied only if all elements in the aggregate type it checks satisfy the constraint.
 *
 * @param <E> the type of elements {@code <A>} holds
 */
public final class ComponentConstraint<E> extends ValuedConstraint<Set<Constraint<? super E>>, List<E>> {
    public ComponentConstraint(Set<Constraint<? super E>> constraints) {
        super(ConstraintType.COMPONENTS_MATCH, constraints);
    }

    @Override
    public TestResult<List<E>> test(List<E> values) {
        List<E> corrected = new ArrayList<>();
        boolean valid = true;
        for (E e : values) {
            for (Constraint<? super E> constraint : this.getConstraintValue()) {
                // there should be no type variance in element correction
                @SuppressWarnings("unchecked") TestResult<E> elementResult = (TestResult<E>) constraint.test(e);
                if (elementResult.hasPassed()) {
                    corrected.add(e);
                } else {
                    valid = false;
                    Optional<E> correctedValue = elementResult.getCorrectedValue();
                    if (correctedValue.isPresent()) {
                        corrected.add(correctedValue.get());
                    } else {
                        return TestResult.unrecoverable();
                    }
                }
            }
        }
        return valid ? TestResult.successful(values) : TestResult.failed(corrected);
    }
}
