package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigLeafBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;

/**
 * A {@code ConfigNode} with some value of type {@code T}.
 *
 * @param <T> The type of value this class holds
 * @see ConfigNode
 * @see ConfigLeafBuilder
 */
public interface ConfigLeaf<T> extends ConfigNode, Property<T>, Commentable {
	/**
	 * Sets the value held by this {@code ConfigLeaf}.
	 *
	 * <p>If the provided value does not satisfy this setting's
	 * {@linkplain SerializableType#test(Object) type constraints}:
	 * <ul>
	 *     <li> if a corrected value can be found, this setting is set to the corrected value
	 *          and this method returns {@code true}. </li>
	 *     <li> otherwise, the current value is not updated and the method returns {@code false}. </li>
	 * </ul>
	 *
	 * @param value the new value this {@code ConfigLeaf} should hold
	 * @return {@code true} if this property changed as a result of the call, and {@code false} otherwise.
	 * @see ConfigLeaf#accepts(Object)
	 */
	@Override
	boolean setValue(T value);

	/**
	 * Returns {@code true} if this property can be set to the given raw value.
	 *
	 * <p>This method does not account for possible corrections offered by the type's constraints.
	 * In other words, it returns {@code true} if and only if every constraint of this property's
	 * {@linkplain #getConfigType() config type} accepts the given value as is.
	 *
	 * @param rawValue the value to check
	 * @return {@code true} if this property accepts the given value, {@code false} otherwise.
	 * @see SerializableType#accepts(Object)
	 */
	default boolean accepts(T rawValue) {
		return true;
	}

	/**
	 * Returns this {@code ConfigLeaf}'s current value.
	 *
	 * <p>If no successful call to {@link #setValue(Object)} has been made,
	 * this method returns this node's {@linkplain #getDefaultValue() default value}.
	 *
	 * @return this node's value
	 */
	@Override
	T getValue();

	SerializableType<T> getConfigType();

	@Override
	default Class<T> getType() {
		return this.getConfigType().getPlatformType();
	}

	/**
	 * Returns the listener for this item.
	 *
	 * <p>When this item's value changes, the consumer will be called with the old value as first argument and the new value as second argument.
	 *
	 * @return the listener
	 */
	@Nonnull
	BiConsumer<T, T> getListener();

	void addChangeListener(BiConsumer<T, T> listener);

	/**
	 * Returns the default value for this item.
	 *
	 * @return the default value
	 */
	@Nullable
	T getDefaultValue();
}
