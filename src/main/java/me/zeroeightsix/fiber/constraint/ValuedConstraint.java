package me.zeroeightsix.fiber.constraint;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A constraint that has a value attached to it.
 *
 * <p> For example, a {@link RegexConstraint} has as value its regular expression pattern.
 *
 * @param <C> the value of this constraint
 * @param <V> the type of values this constraint checks
 */
public abstract class ValuedConstraint<C, V> extends Constraint<V> {

	private final C value;

	public ValuedConstraint(ConstraintType type, @Nonnull C value) {
		super(type);
		this.value = value;
	}

	public C getConstraintValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ValuedConstraint<?, ?> that = (ValuedConstraint<?, ?>) o;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
