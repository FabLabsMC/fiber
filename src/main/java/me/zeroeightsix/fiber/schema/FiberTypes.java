package me.zeroeightsix.fiber.schema;

import me.zeroeightsix.fiber.constraint.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FiberTypes {

    /* Number-derived types */

    public static final ConvertibleType<BigDecimal, BigDecimal> UNBOUNDED_DECIMAL =
            ConvertibleType.decimal();
    public static final ConvertibleType<BigInteger, BigDecimal> UNBOUNDED_INTEGER =
            ConvertibleType.derived(ConvertibleType.decimal(new RangeConstraint(new NumberRange(null, null, BigDecimal.ONE))), BigInteger.class, BigDecimal::new, BigDecimal::toBigInteger);
    public static final ConvertibleType<Byte, BigDecimal> BYTE =
            makeNumber(Byte.class, BigDecimal::new, BigDecimal::byteValue, Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 1);
    public static final ConvertibleType<Short, BigDecimal> SHORT =
            makeNumber(Short.class, BigDecimal::new, BigDecimal::shortValue, Short.MIN_VALUE, Short.MAX_VALUE, (short) 1);
    public static final ConvertibleType<Integer, BigDecimal> INT =
            makeNumber(Integer.class, BigDecimal::new, BigDecimal::intValue, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    public static final ConvertibleType<Long, BigDecimal> LONG =
            makeNumber(Long.class, BigDecimal::new, BigDecimal::longValue, Long.MIN_VALUE, Long.MAX_VALUE, 1L);
    public static final ConvertibleType<Float, BigDecimal> FLOAT =
            makeNumber(Float.class, BigDecimal::new, BigDecimal::floatValue, Float.NEGATIVE_INFINITY, Float.MAX_VALUE, Float.POSITIVE_INFINITY);
    public static final ConvertibleType<Double, BigDecimal> DOUBLE =
            makeNumber(Double.class, BigDecimal::new, BigDecimal::doubleValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.MIN_VALUE);

    public static <N> ConvertibleType<N, BigDecimal> makeNumber(Class<N> actualType, Function<N, BigDecimal> f0, Function<BigDecimal, N> f, N minValue, N maxValue, N precision) {
        final NumberRange acceptedValues = new NumberRange(f0.apply(minValue), f0.apply(maxValue), f0.apply(precision));
        return ConvertibleType.derived(ConvertibleType.decimal(new RangeConstraint(acceptedValues)), actualType, f0, f);
    }

    /* String-derived types */

    public static final ConvertibleType<String, String> STRING = ConvertibleType.string();

    public static <E extends Enum<E>> ConvertibleType<E, String> makeEnum(Class<E> enumType) {
        Set<String> validValues = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.toSet());
        return ConvertibleType.derived(ConvertibleType.string(new EnumConstraint<>(validValues)), enumType, Enum::name, v0 -> Enum.valueOf(enumType, v0));
    }

    /* List-derived types */

    public static final ConvertibleType<List<String>, List<String>> STRING_LIST = ConvertibleType.list(STRING);
    public static final ConvertibleType<Set<String>, List<String>> STRING_SET = makeCollection(STRING, Set.class, HashSet::new, UniqueConstraint.instance());

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E0, E> ConvertibleType<Set<E>, List<E0>> makeSet(ConvertibleType<E, E0> elementType, Constraint<List<E0>>... constraints) {
        return makeCollection(elementType, Set.class, HashSet::new, constraints);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E0, E, C extends Collection<E>> ConvertibleType<C, List<E0>> makeCollection(ConvertibleType<E, E0> elementType, Class<? super C> actualType, Function<List<E>, C> f, Constraint<List<E0>>... constraints) {
        return ConvertibleType.derived(ConvertibleType.list(elementType, constraints), actualType, c -> Collections.unmodifiableList(new ArrayList<>(c)), f);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E0, E> ConvertibleType<E[], List<E0>> makeArray(ConvertibleType<E, E0> elementType, Class<E[]> actualType, Constraint<List<E0>>... constraints) {
        @SuppressWarnings("unchecked") E[] z = (E[]) Array.newInstance(actualType);
        return ConvertibleType.derived(ConvertibleType.list(elementType, constraints), actualType, c -> Collections.unmodifiableList(Arrays.asList(c)), l -> l.toArray(z));
    }
}
