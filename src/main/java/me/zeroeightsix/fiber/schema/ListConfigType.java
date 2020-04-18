package me.zeroeightsix.fiber.schema;

import me.zeroeightsix.fiber.constraint.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class ListConfigType<T> extends ConfigType<T, List<?>> {
    // note: this class could be made type-safe by adding two type parameters <E0> and <U extends ConfigType<?, E0>>
    // but we should probably wait until we target Java 11 (var) to add those
    private final ConfigType<?, ?> elementType;

    public static <E, E0> ListConfigType<List<E>> of(ConfigType<E, E0> elementType) {
        @SuppressWarnings({"unchecked", "rawtypes"}) Constraint<? super List<?>> componentConstraint = (Constraint) new ComponentConstraint<>(Collections.unmodifiableSet(new LinkedHashSet<>(elementType.getConstraints())));
        return new ListConfigType<>(List.class, elementType, v -> {
            List<E0> ret = new ArrayList<>();
            for (E e : v) {
                ret.add(elementType.toRawType(e));
            }
            return Collections.unmodifiableList(ret);
        }, v0 -> {
            List<E> ret = new ArrayList<>();
            for (Object e : v0) {
                ret.add(elementType.toActualType(elementType.getRawType().cast(e)));
            }
            return Collections.unmodifiableList(ret);
        }, Collections.singletonMap(ConstraintType.COMPONENTS_MATCH, componentConstraint));
    }

    @SuppressWarnings("unchecked")
    private ListConfigType(Class<? super T> actualType, ConfigType<?, ?> elementType, Function<T, List<?>> f0, Function<List<?>, T> f, Map<ConstraintType, Constraint<? super List<?>>> typeConstraints) {
        super((Class<T>) actualType, (Class<List<?>>) (Class<?>) List.class, f0, f, typeConstraints);
        this.elementType = elementType;
    }

    @Override
    public Kind getKind() {
        return Kind.LIST;
    }

    @Nonnull
    public ConfigType<?, ?> getElementType() {
        return this.elementType;
    }

    @Override
    public <T1> ListConfigType<T1> derive(Class<? super T1> actualType, Function<T1, T> f0, Function<T, T1> f) {
        return new ListConfigType<>(actualType, this.getElementType(), v -> this.toRawType(f0.apply(v)), v -> f.apply(this.toActualType(v)), this.indexedConstraints);
    }

    public ListConfigType<T> withMinSize(int min) {
        return this.withConstraint(LengthConstraint.min(List::size, min), (current, added) -> current <= added);
    }

    public ListConfigType<T> withMaxSize(int max) {
        return this.withConstraint(LengthConstraint.max(List::size, max), (current, added) -> current >= added);
    }

    public ListConfigType<T> withUniqueElements() {
        Map<ConstraintType, Constraint<? super List<?>>> newConstraints = new EnumMap<>(this.indexedConstraints);
        @SuppressWarnings({"unchecked", "rawtypes"}) Constraint<? super List<?>> uniqueConstraint = (Constraint) UniqueConstraint.instance();
        newConstraints.put(ConstraintType.UNIQUE, uniqueConstraint);
        return new ListConfigType<>(this.getActualType(), this.getElementType(), this.f0, this.f, newConstraints);
    }

    @Override
    public String toString() {
        return this.getActualType().getTypeName() + "<" + this.getElementType().getActualType().getSimpleName() + ">(" + this.getKind() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ListConfigType<?> that = (ListConfigType<?>) o;
        return this.elementType.equals(that.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.elementType);
    }

    private <V, C extends ValuedConstraint<V, ? super List<?>>> ListConfigType<T> withConstraint(C added, BiPredicate<V, V> check) {
        return new ListConfigType<>(this.getActualType(), this.getElementType(), this.f0, this.f, this.updateConstraints(added, check));
    }
}
