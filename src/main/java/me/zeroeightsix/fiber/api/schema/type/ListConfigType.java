package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.ListTypeChecker;

import java.util.List;
import java.util.Objects;

/**
 *
 * @param <E> the type of elements objects of this type hold
 */
public final class ListConfigType<E> extends ConfigType<List<E>> {

    public static <E0> ListConfigType<E0> of(ConfigType<E0> elementType) {
        return new ListConfigType<>(elementType, 0, Integer.MAX_VALUE, false);
    }

    private final ListTypeChecker<E> constraint;
    private final ConfigType<E> elementType;
    private final boolean unique;
    private final int minSize;
    private final int maxSize;

    @SuppressWarnings("unchecked")
    public ListConfigType(ConfigType<E> elementType, int minSize, int maxSize, boolean unique) {
        super((Class<List<E>>) (Class<?>) List.class);
        this.elementType = elementType;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.unique = unique;
        this.constraint = new ListTypeChecker<>(this);
    }

    public ConfigType<E> getElementType() {
        return this.elementType;
    }

    public int getMinSize() {
        return this.minSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public boolean hasUniqueElements() {
        return this.unique;
    }

    @Override
    public <S> void serialize(TypeSerializer<S> serializer, S target) {
        serializer.serialize(this, target);
    }

    @Override
    protected ListTypeChecker<E> getConstraint() {
        return this.constraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ListConfigType<?> that = (ListConfigType<?>) o;
        return this.unique == that.unique &&
                this.minSize == that.minSize &&
                this.maxSize == that.maxSize &&
                Objects.equals(this.elementType, that.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.elementType, this.unique, this.minSize, this.maxSize);
    }
}
