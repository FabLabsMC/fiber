package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.ListSerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;

import java.util.*;

/**
 * A component constraints is satisfied only if all elements in the aggregate type it checks satisfy the constraint.
 *
 * @param <E> the type of elements {@code <A>} holds
 */
public final class ListTypeChecker<E> extends Constraint<List<E>, ListSerializableType<E>> {

    public ListTypeChecker(ListSerializableType<E> cfg) {
        super(cfg);
    }

    @Override
    public TypeCheckResult<List<E>> test(List<E> values) {
        boolean valid = true;
        int maxSize = this.cfg.getMaxSize();
        Collection<E> corrected = this.cfg.hasUniqueElements() ? new LinkedHashSet<>(values.size()) : new ArrayList<>(values.size());

        for (E e : values) {
            if (corrected.size() >= maxSize) {
                valid = false;
                break;
            }
            TypeCheckResult<E> testResult = this.cfg.getElementType().test(e);
            if (testResult.hasPassed()) {
                valid &= corrected.add(e);  // UNIQUE check
            } else {
                valid = false;
                Optional<E> correctedValue = testResult.getCorrectedValue();
                correctedValue.ifPresent(corrected::add);
                // if not present, just skip it
            }
        }
        if (corrected.size() < this.cfg.getMinSize()) {
            return TypeCheckResult.unrecoverable();
        }

        return valid ? TypeCheckResult.successful(values) : TypeCheckResult.failed(new ArrayList<>(corrected));
    }

    @Override
    public boolean comprehends(Constraint<?, ?> constraint) {
        if (!(constraint instanceof ListTypeChecker)) return false;
        ListTypeChecker<?> that = (ListTypeChecker<?>) constraint;
        if (this.cfg.getMinSize() > that.cfg.getMinSize()) {
            return false;
        }
        if (this.cfg.getMaxSize() < that.cfg.getMaxSize()) {
            return false;
        }
        if (!this.cfg.getElementType().isAssignableFrom(that.cfg.getElementType())) {
            return false;
        }
        // "not unique" comprehends unique
        return !this.cfg.hasUniqueElements() || that.cfg.hasUniqueElements();
    }
}
