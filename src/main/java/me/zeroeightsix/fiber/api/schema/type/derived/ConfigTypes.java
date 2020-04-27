package me.zeroeightsix.fiber.api.schema.type.derived;


import me.zeroeightsix.fiber.api.schema.type.*;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ConfigTypes {

    public static final BooleanConfigType<Boolean> BOOLEAN =
            new BooleanConfigType<>(Boolean.class, Function.identity(), Function.identity());

    /* Number-derived types */

    public static final NumberConfigType<BigDecimal> UNBOUNDED_DECIMAL =
            makeNumber(BigDecimal.class, Function.identity(), Function.identity(), null, null, null);
    public static final NumberConfigType<BigInteger> UNBOUNDED_INTEGER =
            makeNumber(BigInteger.class, BigDecimal::new, BigDecimal::toBigInteger, null, null, null);
    public static final NumberConfigType<Byte> BYTE =
            makeNumber(Byte.class, BigDecimal::valueOf, BigDecimal::byteValue, Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 1);
    public static final NumberConfigType<Short> SHORT =
            makeNumber(Short.class, BigDecimal::valueOf, BigDecimal::shortValue, Short.MIN_VALUE, Short.MAX_VALUE, (short) 1);
    public static final NumberConfigType<Integer> INTEGER =
            makeNumber(Integer.class, BigDecimal::valueOf, BigDecimal::intValue, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    public static final NumberConfigType<Long> LONG =
            makeNumber(Long.class, BigDecimal::valueOf, BigDecimal::longValue, Long.MIN_VALUE, Long.MAX_VALUE, 1L);
    public static final NumberConfigType<Float> FLOAT =
            makeNumber(Float.class, BigDecimal::valueOf, BigDecimal::floatValue, null, null, null);
    public static final NumberConfigType<Double> DOUBLE =
            makeNumber(Double.class, BigDecimal::valueOf, BigDecimal::doubleValue, null, null, null);

    public static <N> NumberConfigType<N> makeNumber(Class<N> actualType, Function<N, BigDecimal> serializer, Function<BigDecimal, N> deserializer, @Nullable N minValue, @Nullable N maxValue, @Nullable N precision) {
        return new NumberConfigType<>(
                new DecimalSerializableType(minValue == null ? null : serializer.apply(minValue), maxValue == null ? null : serializer.apply(maxValue), precision == null ? null : serializer.apply(precision)),
                actualType,
                deserializer,
                serializer
        );
    }

    /* String-derived types */

    public static final StringConfigType<String> STRING =
            new StringConfigType<>(StringSerializableType.DEFAULT_STRING, String.class, Function.identity(), Function.identity());

    public static final StringConfigType<Character> CHARACTER =
            STRING.withMinLength(1).withMaxLength(1).derive(Character.class, s -> s.charAt(0), Object::toString);

    /* Enum-derived types */

    public static <E extends Enum<E>> EnumConfigType<E> makeEnum(Class<E> enumType) {
        Set<String> validValues = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.toSet());
        return new EnumConfigType<>(new EnumSerializableType(validValues), enumType, e -> Enum.valueOf(enumType, e), Enum::name);
    }

    /* List-derived types */

    public static <E0, E, U extends ConfigType<E, E0, ?>> ListConfigType<List<E>, E0> makeList(U elementType) {
        return new ListConfigType<>(
                new ListSerializableType<>(elementType.getSerializedType()), List.class,
                l -> {
                    List<E> ret = new ArrayList<>();
                    for (E0 e0 : l) {
                        ret.add(elementType.toRuntimeType(e0));
                    }
                    return Collections.unmodifiableList(ret);
                },
                l -> {
                    List<E0> ret = new ArrayList<>();
                    for (E e : l) {
                        ret.add(elementType.toPlatformType(e));
                    }
                    return Collections.unmodifiableList(ret);
                }
        );
    }

    public static <E0, E, U extends ConfigType<E, E0, ?>> ListConfigType<Set<E>, E0> makeSet(U elementType) {
        return new ListConfigType<>(
                new ListSerializableType<>(elementType.getSerializedType(), 0, Integer.MAX_VALUE, true),
                Set.class,
                l -> {
                    Set<E> ret = new HashSet<>();
                    for (E0 e0 : l) {
                        ret.add(elementType.toRuntimeType(e0));
                    }
                    return Collections.unmodifiableSet(ret);
                },
                l -> {
                    List<E0> ret = new ArrayList<>();
                    for (E e : l) {
                        ret.add(elementType.toPlatformType(e));
                    }
                    return Collections.unmodifiableList(ret);
                }
        );
    }

    public static <E0, E, U extends ConfigType<E, E0, ? extends SerializableType<E0>>> ListConfigType<E[], E0> makeArray(U elementType) {
        @SuppressWarnings("unchecked") Class<E[]> arrType = (Class<E[]>) Array.newInstance(elementType.getRuntimeType(), 0).getClass();
        return new ListConfigType<>(
                new ListSerializableType<>(elementType.getSerializedType()),
                arrType,
                l -> {
                    @SuppressWarnings("unchecked") E[] ret = (E[]) Array.newInstance(elementType.getRuntimeType(), l.size());
                    for (int i = 0; i < l.size(); i++) {
                        ret[i] = elementType.toRuntimeType(l.get(i));
                    }
                    return ret;
                },
                arr -> {
                    List<E0> ret = new ArrayList<>();
                    for (E e : arr) {
                        ret.add(elementType.toPlatformType(e));
                    }
                    return Collections.unmodifiableList(ret);
                }
        );
    }

    /* Record-derived config types */

    public static <K, V, S> MapConfigType<Map<K, V>, S> makeMap(StringConfigType<K> keyConverter, ConfigType<V, S, ?> valueConverter) {
        return new MapConfigType<>(
                new MapSerializableType<>(keyConverter.getSerializedType(), valueConverter.getSerializedType()),
                Map.class,
                map -> {
                    Map<K, V> ret = new HashMap<>();
                    map.forEach((k, v) -> ret.put(keyConverter.toRuntimeType(k), valueConverter.toRuntimeType(v)));
                    return Collections.unmodifiableMap(ret);
                },
                map -> {
                    Map<String, S> ret = new HashMap<>();
                    map.forEach((k, v) -> ret.put(keyConverter.toPlatformType(k), valueConverter.toPlatformType(v)));
                    return ret;
                }
        );
    }
}
