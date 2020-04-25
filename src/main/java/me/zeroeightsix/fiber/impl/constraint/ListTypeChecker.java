package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.TypeCheckResult;
import me.zeroeightsix.fiber.api.schema.type.ListConfigType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A component constraints is satisfied only if all elements in the aggregate type it checks satisfy the constraint.
 *
 * @param <E> the type of elements {@code <A>} holds
 */
public final class ListTypeChecker<E> extends Constraint<List<E>, ListConfigType<E>> {

    public ListTypeChecker(ListConfigType<E> cfg) {
        super(cfg);
    }

    @Override
    public TypeCheckResult<List<E>> test(List<E> values) {
        int correctedSize = Math.min(this.cfg.getMaxSize(), values.size());
        boolean valid = values.size() == correctedSize;
        Collection<E> corrected = this.cfg.hasUniqueElements() ? new LinkedHashSet<>(correctedSize) : new ArrayList<>(correctedSize);

        for (int i = 0; i < correctedSize; i++) {
            E e = values.get(i);
            TypeCheckResult<E> testResult = this.cfg.getElementType().test(e);
            if (testResult.hasPassed()) {
                valid &= corrected.add(e);  // UNIQUE check
            } else {
                valid = false;
                testResult.getCorrectedValue().ifPresent(corrected::add);
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
