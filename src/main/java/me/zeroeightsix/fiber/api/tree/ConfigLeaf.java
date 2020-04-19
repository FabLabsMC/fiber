package me.zeroeightsix.fiber.api.tree;

import me.zeroeightsix.fiber.api.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.impl.tree.ConfigNodeImpl;
import me.zeroeightsix.fiber.schema.ConfigType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

/**
 * A {@code ConfigNode} with some value of type {@code T}.
 *
 * @param <T>  The type of value this class holds
 * @param <T0> The base type to which values can be converted
 * @see ConfigNode
 * @see ConfigLeafBuilder
 */
public interface ConfigLeaf<T, T0> extends ConfigNode, Property<T>, Commentable {

    /**
     * Sets the value held by this {@code ConfigLeaf}.
     *
     * <p> If the provided value does not satisfy this setting's
     * {@linkplain ConfigType#getConstraints() type constraints}:
     * <ul>
     *     <li> if a corrected value can be found, this setting is set to the corrected value
     *          and this method returns {@code true}. </li>
     *     <li> otherwise, the current value is not updated and the method returns {@code false}. </li>
     * </ul>
     *
     * @param value the new value this {@code ConfigLeaf} should hold
     * @return {@code true} if this property changed as a result of the call, and {@code false} otherwise.
     * @see Property#accepts(Object)
     */
    @Override
    boolean setValue(T value);

    /**
     * Returns this {@code ConfigLeaf}'s current value.
     *
     * <p> If no successful call to {@link #setValue(Object)} has been made,
     * this method returns this node's {@linkplain #getDefaultValue() default value}.
     *
     * @return this node's value
     */
    @Override
    T getValue();

    ConfigType<T, T0> getConfigType();

    /**
     * Returns {@code true} if this property can be set to the given raw value.
     *
     * <p> This method does not account for possible corrections offered by the type's constraints.
     * In other words, it returns {@code true} if and only if every constraint of this property's
     * {@linkplain #getConfigType() config type} accepts the given value as is.
     *
     * @param rawValue the value to check
     * @return {@code true} if this property accepts the given value, {@code false} otherwise.
     * @see #setValueFrom(Object)
     * @see ConfigType#getConstraints()
     */
    default boolean acceptsRaw(T0 rawValue) {
        return true;
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
     * Sets the value of this property from a raw value.
     *
     * <p> The provided value will be converted to this property's
     * actual type using {@link ConfigType#toActualType(Object)}.
     *
     * <p> This can fail and return {@code false} if, for example, the value did not satisfy the constraints of the property.
     *
     * @param rawValue the new value this property should receive
     * @return {@code true} if this property changed as a result of the call
     * @see #acceptsRaw(Object)
     */
    boolean setValueFrom(T0 rawValue);

    T0 getRawValue();
}
