package me.zeroeightsix.fiber.api.tree;

import me.zeroeightsix.fiber.api.schema.ConfigType;

/**
 * A {@code Property} that delegates all operations to another.
 *
 * <p> This can be used in conjunction with config builders to
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
public class PropertyMirror<T> implements Property<T> {
    protected Property<T> delegate;

    /**
     * Sets a property to mirror.
     *
     * <p> After calling this method with a valid delegate,
     * every property method will redirect to {@code delegate}.
     *
     * @param delegate a property to mirror
     */
    public void mirror(Property<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean setValue(T value) {
        return this.delegate != null && this.delegate.setValue(value);
    }

    @Override
    public boolean accepts(T rawValue) {
        return this.delegate != null && this.delegate.accepts(rawValue);
    }

    @Override
    public T getValue() {
        if (this.delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
        return this.delegate.getValue();
    }

    @Override
    public ConfigType<T, ?> getConfigType() {
        if (this.delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
        return this.delegate.getConfigType();
    }
}