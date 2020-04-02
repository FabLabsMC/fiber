package me.zeroeightsix.fiber.tree;

/**
 * A {@code Property} that delegates all operations to another.
 *
 * <p> This can be used in conjunction with config builders to
 * easily setup a configuration without reflection. For example:
 * <pre>{@code
 * public final PropertyMirror<Integer> diamondsDropped = new PropertyMirror<>();
 *
 * {
 *     ConfigNode.builder()
 *     .beginValue("diamondsDropped", Integer.class)
 *      .beginConstraints().atLeast(1).finishConstraints()
 *     .finishValue(diamondsDropped::mirror)
 *     .build();
 * }
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
    public Class<T> getType() {
        if (delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
        return delegate.getType();
    }
}
