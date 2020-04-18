package me.zeroeightsix.fiber.schema;

import me.zeroeightsix.fiber.constraint.ComponentConstraint;
import me.zeroeightsix.fiber.constraint.Constraint;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public final class ConvertibleType<T, T0> {
    public static <T0, T1, T> ConvertibleType<T, T0> derived(ConvertibleType<T1, T0> baseType, Class<? super T> actualType, Function<T, T1> f0, Function<T1, T> f) {
        @SuppressWarnings("unchecked") Class<T> c = (Class<T>) actualType;
        return new ConvertibleType<>(c, baseType.getRawType(), baseType.getElementType(), v -> baseType.toRawType(f0.apply(v)), v -> f.apply(baseType.toActualType(v)), Collections.emptyList());
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static ConvertibleType<BigDecimal, BigDecimal> decimal(Constraint<BigDecimal>... constraints) {
        return new ConvertibleType<>(BigDecimal.class, BigDecimal.class, null, Function.identity(), Function.identity(), Arrays.asList(constraints));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static ConvertibleType<String, String> string(Constraint<String>... constraints) {
        return new ConvertibleType<>(String.class, String.class, null, Function.identity(), Function.identity(), Arrays.asList(constraints));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E0, E> ConvertibleType<List<E>, List<E0>> list(ConvertibleType<E, E0> elementType, Constraint<List<E0>>... constraints) {
        Set<Constraint<? super List<E0>>> allConstraints = new LinkedHashSet<>(Arrays.asList(constraints));
        Set<Constraint<? super E0>> elementConstraints = elementType.getTypeConstraints();
        allConstraints.add(new ComponentConstraint<>(elementConstraints));
        @SuppressWarnings("unchecked") Class<List<E>> c = (Class<List<E>>) (Class<?>) List.class;
        @SuppressWarnings("unchecked") Class<List<E0>> c0 = (Class<List<E0>>) (Class<?>) List.class;
        return new ConvertibleType<>(c, c0, elementType, v -> {
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
        }, allConstraints);
    }

    private final Class<T> actualType;
    private final Class<T0> rawType;
    private final Set<Constraint<? super T0>> typeConstraints;
    private final Function<T, T0> f0;
    private final Function<T0, T> f;
    private final ConvertibleType<?, ?> elementType;

    private ConvertibleType(Class<T> actualType, Class<T0> rawType, ConvertibleType<?, ?> elementType, Function<T, T0> f0, Function<T0, T> f, Collection<Constraint<? super T0>> typeConstraints) {
        this.actualType = actualType;
        this.rawType = rawType;
        this.elementType = elementType;
        this.typeConstraints = Collections.unmodifiableSet(new LinkedHashSet<>(typeConstraints));
        this.f0 = f0;
        this.f = f;
    }

    public Class<T> getActualType() {
        return this.actualType;
    }

    public Class<T0> getRawType() {
        return this.rawType;
    }

    @Nullable
    public ConvertibleType<?, ?> getElementType() {
        return this.elementType;
    }

    public T0 toRawType(T actualValue) {
        return f0.apply(actualValue);
    }

    public T toActualType(T0 rawValue) {
        return f.apply(rawValue);
    }

    public Set<Constraint<? super T0>> getTypeConstraints() {
        return this.typeConstraints;
    }

    public boolean isNumber() {
        return this.rawType == BigDecimal.class;
    }

    public boolean isString() {
        return this.rawType == String.class;
    }

    public boolean isList() {
        return this.rawType == List.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConvertibleType<?, ?> that = (ConvertibleType<?, ?>) o;
        return Objects.equals(actualType, that.actualType) &&
                Objects.equals(rawType, that.rawType) &&
                Objects.equals(typeConstraints, that.typeConstraints) &&
                Objects.equals(elementType, that.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actualType, rawType, typeConstraints, elementType);
    }
}
