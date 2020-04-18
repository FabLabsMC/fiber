package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.schema.ConvertibleType;

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
public class PropertyMirror<T, T0> implements Property<T, T0> {
    protected Property<T, T0> delegate;

    /**
     * Sets a property to mirror.
     *
     * <p> After calling this method with a valid delegate,
     * every property method will redirect to {@code delegate}.
     *
     * @param delegate a property to mirror
     */
    public void mirror(Property<T, T0> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean setValue(T value) {
        return delegate != null && delegate.setValue(value);
    }

    @Override
    public boolean accepts(T value) {
        return delegate != null && delegate.accepts(value);
    }

    @Override
    public T getValue() {
        if (delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
        return delegate.getValue();
    }

    @Override
    public T0 getRawValue() {
        if (delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
        return delegate.getRawValue();
    }

    @Override
    public ConvertibleType<T, T0> getConvertibleType() {
        if (delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
        return delegate.getConvertibleType();
    }
}
