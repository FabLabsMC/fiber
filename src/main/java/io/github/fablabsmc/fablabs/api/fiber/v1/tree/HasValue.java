package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

import javax.annotation.Nonnull;

/**
 * Indicates that this class holds some nullable value.
 *
 * @param <T> The type of the value being held.
 */
public interface HasValue<T> {
	/**
	 * Returns the value being held.
	 *
	 * @return the value
	 */
	@Nonnull
	T getValue();

	/**
	 * Returns the class of the type of the value being held.
	 *
	 * @return the class of the type of the value
	 */
	Class<? super T> getType();
}
