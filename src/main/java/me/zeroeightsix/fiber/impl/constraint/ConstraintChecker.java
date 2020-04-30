package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;

/**
 * Checks the validity of values based on a {@link SerializableType}'s constraints.
 *
 * @param <V> the type of values this constraint checks
 * @see ConfigLeaf
 * @see SerializableType
 */
public abstract class ConstraintChecker<V, T extends SerializableType<V>> {
    ConstraintChecker() {
    }

    /**
     * Tests a value against this {@code Constraint}.
     *
     * <p>This method may provide a corrected value that can be used
     * if the input value is invalid.
     *
     * @param cfg   the type configuration to test against
     * @param value the value
     * @return {@code true} if {@code value} satisfies the constraint
     */
    public abstract TypeCheckResult<V> test(T cfg, V value);

    /**
     * Returns {@code true} if {@code cfg} comprehends {@code cfg2}.
     *
     * <p>A type configuration comprehends another if it accepts every
     * value that the other does.
     * <pre>forall x, cfg2.accepts(x) =&gt; cfg.accepts(x)</pre>
     *
     * @param cfg  the tested comprehensive type configuration
     * @param cfg2 the tested comprehended type configuration
     * @return {@code true} if {@code cfg} comprehends {@code cfg2},
     * otherwise {@code false}.
     * @see SerializableType#isAssignableFrom(SerializableType)
     */
    public abstract boolean comprehends(T cfg, T cfg2);
}
