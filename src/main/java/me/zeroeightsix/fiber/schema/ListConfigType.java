package me.zeroeightsix.fiber.schema;

import me.zeroeightsix.fiber.constraint.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ListConfigType<T, E0, U extends ConfigType<?, E0>> extends ConfigType<T, List<E0>> {
    private final U elementType;

    public static <E, E0, U extends ConfigType<E, E0>> ListConfigType<List<E>, E0, U> of(U elementType) {
        ComponentConstraint<E0> componentConstraint = new ComponentConstraint<>(elementType.getTypeConstraints());
        return new ListConfigType<>(List.class, elementType, v -> {
            List<E0> ret = new ArrayList<>();
            for (E e : v) {
                ret.add(elementType.toRawType(e));
            }
            return Collections.unmodifiableList(ret);
        }, v0 -> {
            List<E> ret = new ArrayList<>();
            for (E0 e : v0) {
                ret.add(elementType.toActualType(e));
            }
            return Collections.unmodifiableList(ret);
        }, Collections.singletonMap(ConstraintType.COMPONENTS_MATCH, componentConstraint));
    }

    @SuppressWarnings("unchecked")
    private ListConfigType(Class<? super T> actualType, U elementType, Function<T, List<E0>> f0, Function<List<E0>, T> f, Map<ConstraintType, Constraint<? super List<E0>>> typeConstraints) {
        super((Class<T>) actualType, (Class<List<E0>>) (Class<?>) List.class, f0, f, typeConstraints);
        this.elementType = elementType;
    }

    @Nullable
    public U getElementType() {
        return this.elementType;
    }

    @Override
    public <T1> ListConfigType<T1, E0, U> derive(Class<? super T1> actualType, Function<T1, T> f0, Function<T, T1> f) {
        return new ListConfigType<>(actualType, this.getElementType(), v -> this.toRawType(f0.apply(v)), v -> f.apply(this.toActualType(v)), this.indexedConstraints);
    }

    public ListConfigType<T, E0, U> withMinSize(int min) {
        return this.withConstraint(LengthConstraint.min(List::size, min), (current, added) -> current <= added);
    }

    public ListConfigType<T, E0, U> withMaxSize(int max) {
        return this.withConstraint(LengthConstraint.max(List::size, max), (current, added) -> current >= added);
    }

    public ListConfigType<T, E0, U> withUniqueElements() {
        Map<ConstraintType, Constraint<? super List<E0>>> newConstraints = new EnumMap<>(this.indexedConstraints);
        newConstraints.put(ConstraintType.UNIQUE, UniqueConstraint.instance());
        return new ListConfigType<>(this.getActualType(), this.getElementType(), this.f0, this.f, newConstraints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ListConfigType<?, ?, ?> that = (ListConfigType<?, ?, ?>) o;
        return elementType.equals(that.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elementType);
    }

    private <V, C extends ValuedConstraint<V, ? super List<E0>>> ListConfigType<T, E0, U> withConstraint(C added, BiPredicate<V, V> check) {
        return new ListConfigType<>(this.getActualType(), this.getElementType(), this.f0, this.f, this.updateConstraints(added, check));
    }
}
