package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.api.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.impl.constraint.FinalConstraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A {@code ConfigNode} with some value of type {@code T}.
 *
 * @param <T> The type of value this class holds
 * @see ConfigNodeImpl
 * @see ConfigLeafBuilder
 * @see me.zeroeightsix.fiber.api.builder.ConfigAggregateBuilder
 */
public interface ConfigLeaf<T> extends ConfigNode, Property<T>, Commentable {

    /**
     * Sets the value held by this {@code ConfigLeaf}.
     *
     * <p> If the value does not satisfy this setting's {@linkplain #getConstraints() constraints},
     * the current value is not updated and the method returns {@code false}.
     *
     * @param value the new value this {@code ConfigLeaf} should hold
     * @return {@code true} if this property changed as a result of the call, and {@code false} otherwise.
     * @see #accepts(Object)
     */
    @Override
    boolean setValue(T value);

    /**
     * Returns this {@code ConfigLeaf}'s current value.
     *
     * <p> If no successful call to {@link #setValue(Object)} has been made,
     * this method returns this node's {@linkplain #getDefaultValue() default value}.
     * @return this node's value
     */
    @Override
    T getValue();

    /**
     * Returns a class object representing the type of values this node can hold.
     *
     * <p> The returned class object can be used for various runtime type checks,
     * as well as constraint configuration.
     *
     * @return the class representing the type of this node's values
     */
    @Override
    Class<T> getType();

    /**
     * {@inheritDoc}
     *
     * <p> A value is accepted if it satisfies every constraint
     * this setting has. Note that some constraints such as {@link FinalConstraint}
     * will cause even the {@linkplain #getValue() current value} to be rejected.
     *
     * @param value the value to check
     * @see #setValue(Object)
     * @see #getConstraints()
     */
    @Override
    default boolean accepts(T value) {
        return false;
    }

    /**
     * Returns the listener for this item.
     *
     * <p> When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
     *
     * @return the listener
     */
    @Nonnull
    BiConsumer<T, T> getListener();

    /**
     * Returns the default value for this item.
     *
     * @return the default value
     */
    @Nullable
    T getDefaultValue();

    /**
     * Returns the list of constraints for this item.
     *
     * <p> For a call to {@link #setValue} to pass (and such, return {@code true}), the value must satisfy all constraints in this list.
     *
     * @return the list of constraints
     */
    @Nonnull
    List<Constraint<? super T>> getConstraints();
}
