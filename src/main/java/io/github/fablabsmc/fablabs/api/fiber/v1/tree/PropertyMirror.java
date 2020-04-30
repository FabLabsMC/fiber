package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.impl.fiber.tree.PropertyMirrorImpl;

/**
 * A {@code Property} that delegates all operations to another.
 *
 * <p>This can be used in conjunction with config builders to
 * easily setup a configuration without reflection. For example:
 * <pre>{@code
 * public final PropertyMirror<Integer> diamondsDropped = new PropertyMirror<>();
 *
 * private final Node config = ConfigNode.builder()
 *     .beginValue("diamondsDropped", Integer.class)
 *      .beginConstraints().atLeast(1).finishConstraints()
 *     .finishValue(diamondsDropped::mirror)
 *     .build();
 * }</pre>
 *
 * @param <T> the type of value this property mirrors
 */
public interface PropertyMirror<T> extends Property<T> {
	static <T> PropertyMirror<T> create(ConfigType<T, ?, ?> converter) {
		return new PropertyMirrorImpl<>(converter);
	}

	/**
	 * Sets a property to mirror.
	 *
	 * <p>After calling this method with a valid delegate,
	 * every property method will redirect to {@code delegate}.
	 *
	 * @param delegate a property to mirror
	 */
	void mirror(Property<?> delegate);

	Property<?> getMirrored();

	ConfigType<T, ?, ?> getConverter();
}
