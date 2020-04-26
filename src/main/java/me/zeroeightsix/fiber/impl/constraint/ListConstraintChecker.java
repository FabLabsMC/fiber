package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.ListSerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;

import java.util.*;

/**
 * A component constraints is satisfied only if all elements in the aggregate type it checks satisfy the constraint.
 *
 * @param <E> the type of elements {@code <A>} holds
 */
public final class ListConstraintChecker<E> extends ConstraintChecker<List<E>, ListSerializableType<E>> {
    private static final ListConstraintChecker<?> INSTANCE = new ListConstraintChecker<>();

    public static <E> ListConstraintChecker<E> instance() {
        @SuppressWarnings("unchecked") ListConstraintChecker<E> t = (ListConstraintChecker<E>) INSTANCE;
        return t;
    }

    private ListConstraintChecker() { }

    @Override
    public TypeCheckResult<List<E>> test(ListSerializableType<E> cfg, List<E> values) {
        boolean valid = true;
        int maxSize = cfg.getMaxSize();
        Collection<E> corrected = cfg.hasUniqueElements() ? new LinkedHashSet<>(values.size()) : new ArrayList<>(values.size());

        for (E e : values) {
            if (corrected.size() >= maxSize) {
                valid = false;
                break;
            }
            TypeCheckResult<E> testResult = cfg.getElementType().test(e);
            if (testResult.hasPassed()) {
                valid &= corrected.add(e);  // UNIQUE check
            } else {
                valid = false;
                Optional<E> correctedValue = testResult.getCorrectedValue();
                correctedValue.ifPresent(corrected::add);
                // if not present, just skip it
            }
        }
        if (corrected.size() < cfg.getMinSize()) {
            return TypeCheckResult.unrecoverable();
        }

        return valid ? TypeCheckResult.successful(values) : TypeCheckResult.failed(new ArrayList<>(corrected));
    }

    @Override
    public boolean comprehends(ListSerializableType<E> cfg, ListSerializableType<E> cfg2) {
        if (cfg.getMinSize() > cfg2.getMinSize()) {
            return false;
        }
        if (cfg.getMaxSize() < cfg2.getMaxSize()) {
            return false;
        }
        if (!cfg.getElementType().isAssignableFrom(cfg2.getElementType())) {
            return false;
        }
        // "not unique" comprehends unique
        return !cfg.hasUniqueElements() || cfg2.hasUniqueElements();
    }
}
