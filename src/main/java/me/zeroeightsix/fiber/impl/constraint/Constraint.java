package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;

/**
 * Specifies a condition values must satisfy before being set to a {@code ConfigLeaf}.
 *
 * @param <V> the type of values this constraint checks
 * @see ConfigLeaf
 */
public abstract class Constraint<V, T extends SerializableType<V>> {

	protected final T cfg;

	public Constraint(T cfg) {
		this.cfg = cfg;
	}

	/**
	 * Tests a value against this {@code Constraint}.
	 *
	 * <p> This method may provide a corrected value that can be used
	 * if the input value is invalid.
	 *
	 * @param value the value
	 * @return {@code true} if {@code value} satisfies the constraint
	 */
	public abstract TypeCheckResult<V> test(V value);

	public abstract boolean comprehends(Constraint<?, ?> constraint);

}
