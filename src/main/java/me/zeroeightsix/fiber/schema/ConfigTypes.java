package me.zeroeightsix.fiber.schema;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ConfigTypes {

    public static final BinaryConfigType<Boolean> BOOLEAN =
            BinaryConfigType.BOOLEAN;

    /* Number-derived types */

    public static final DecimalConfigType<BigDecimal> UNBOUNDED_DECIMAL =
            DecimalConfigType.UNBOUNDED;
    public static final DecimalConfigType<BigInteger> UNBOUNDED_INTEGER =
            UNBOUNDED_DECIMAL.withIncrement(BigDecimal.ONE).derive(BigInteger.class, BigDecimal::new, BigDecimal::toBigInteger);
    public static final DecimalConfigType<Byte> BYTE =
            makeNumber(Byte.class, BigDecimal::valueOf, BigDecimal::byteValue, Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 1);
    public static final DecimalConfigType<Short> SHORT =
            makeNumber(Short.class, BigDecimal::valueOf, BigDecimal::shortValue, Short.MIN_VALUE, Short.MAX_VALUE, (short) 1);
    public static final DecimalConfigType<Integer> INTEGER =
            makeNumber(Integer.class, BigDecimal::valueOf, BigDecimal::intValue, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    public static final DecimalConfigType<Long> LONG =
            makeNumber(Long.class, BigDecimal::valueOf, BigDecimal::longValue, Long.MIN_VALUE, Long.MAX_VALUE, 1L);
    public static final DecimalConfigType<Float> FLOAT =
            makeNumber(Float.class, BigDecimal::valueOf, BigDecimal::floatValue, Float.NEGATIVE_INFINITY, Float.MAX_VALUE, Float.POSITIVE_INFINITY);
    public static final DecimalConfigType<Double> DOUBLE =
            makeNumber(Double.class, BigDecimal::valueOf, BigDecimal::doubleValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.MIN_VALUE);

    public static <N> DecimalConfigType<N> makeNumber(Class<N> actualType, Function<N, BigDecimal> f0, Function<BigDecimal, N> f, N minValue, N maxValue, N precision) {
        return UNBOUNDED_DECIMAL.withValidRange(f0.apply(minValue), f0.apply(maxValue), f0.apply(precision)).derive(actualType, f0, f);
    }

    /* String-derived types */

    public static final StringConfigType<String> STRING =
            StringConfigType.STRING;

    public static <E extends Enum<E>> ConfigType<E, String> makeEnum(Class<E> enumType) {
        Set<String> validValues = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.toSet());
        return STRING.withValidValues(validValues).derive(enumType, Enum::name, v0 -> Enum.valueOf(enumType, v0));
    }

    /* List-derived types */

    public static final ListConfigType<List<String>> STRING_LIST =
            makeList(STRING);
    public static final ListConfigType<Set<String>> STRING_SET =
            makeSet(STRING);
    public static final ListConfigType<String[]> STRING_ARRAY =
            makeArray(STRING);

    public static <E0, E, U extends ConfigType<E, E0>> ListConfigType<List<E>> makeList(U elementType) {
        return ListConfigType.of(elementType);
    }

    public static <E0, E, U extends ConfigType<E, E0>> ListConfigType<Set<E>> makeSet(U elementType) {
        return ListConfigType.of(elementType).withUniqueElements().derive(Set.class, ArrayList::new, l -> Collections.unmodifiableSet(new LinkedHashSet<>(l)));
    }

    public static <E0, E, U extends ConfigType<E, E0>> ListConfigType<E[]> makeArray(U elementType) {
        @SuppressWarnings("unchecked") E[] z = (E[]) Array.newInstance(elementType.getActualType(), 0);
        @SuppressWarnings("unchecked") Class<E[]> arrType = (Class<E[]>) z.getClass();
        return ListConfigType.of(elementType).derive(arrType, Arrays::asList, l -> l.toArray(z));
    }
}
