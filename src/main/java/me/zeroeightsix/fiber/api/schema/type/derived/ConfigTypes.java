package me.zeroeightsix.fiber.api.schema.type.derived;


import me.zeroeightsix.fiber.api.schema.type.*;
import me.zeroeightsix.fiber.impl.annotation.magic.TypeMagic;

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

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code boolean}. If {@code elementType}
     * represents the type {@link Boolean}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding a {@code boolean[]}.
     */
    public static <S> ListConfigType<boolean[], S> makeBooleanArray(ConfigType<Boolean, S, ?> elementType) {
        return makeArray(boolean[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code byte}. If {@code elementType}
     * represents the type {@link Byte}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding a {@code byte[]}.
     */
    public static <S> ListConfigType<byte[], S> makeByteArray(ConfigType<Byte, S, ?> elementType) {
        return makeArray(byte[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code short}. If {@code elementType}
     * represents the type {@link Short}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding a {@code short[]}.
     */
    public static <S> ListConfigType<short[], S> makeShortArray(ConfigType<Short, S, ?> elementType) {
        return makeArray(short[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code int}. If {@code elementType}
     * represents the type {@link Integer}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding an {@code int[]}.
     */
    public static <S> ListConfigType<int[], S> makeIntArray(ConfigType<Integer, S, ?> elementType) {
        return makeArray(int[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code long}. If {@code elementType}
     * represents the type {@link Long}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding a {@code long[]}.
     */
    public static <S> ListConfigType<long[], S> makeLongArray(ConfigType<Long, S, ?> elementType) {
        return makeArray(long[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code float}. If {@code elementType}
     * represents the type {@link Float}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding a {@code float[]}.
     */
    public static <S> ListConfigType<float[], S> makeFloatArray(ConfigType<Float, S, ?> elementType) {
        return makeArray(float[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code double}. If {@code elementType}
     * represents the type {@link Double}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding a {@code double[]}.
     */
    public static <S> ListConfigType<double[], S> makeDoubleArray(ConfigType<Double, S, ?> elementType) {
        return makeArray(double[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of primitive type {@code char}. If {@code elementType}
     * represents the type {@link Character}, then the element values are unboxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @return A {@link ListConfigType} holding a {@code char[]}.
     */
    public static <S> ListConfigType<char[], S> makeCharArray(ConfigType<Character, S, ?> elementType) {
        return makeArray(char[].class, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array of reference type. If {@code elementType} represents
     * a primitive type, then the element values are boxed before being stored in the array.
     *
     * @param elementType The config type of the array components.
     * @param <S> The backing serialized type.
     * @param <E> The (possibly boxed) array component type.
     * @return A {@link ListConfigType} holding an {@code E[]}.
     */
    public static <S, E> ListConfigType<E[], S> makeArray(ConfigType<E, S, ?> elementType) {
        // need to explicitly wrap the runtime type to avoid sneaky ClassCastException (Class<Integer> could be int.class)
        Class<E> componentType = TypeMagic.wrapPrimitive(elementType.getRuntimeType());
        @SuppressWarnings("unchecked") Class<E[]> arrayType = (Class<E[]>) Array.newInstance(componentType, 0).getClass();
        return makeArray(arrayType, elementType);
    }

    /**
     * Creates a {@link ListConfigType} representing an array type.
     * <p>
     * This internal method is called by the eight primitive array specializations and the object array
     * specialization in order to provide a type-safe interface to this implementation.
     *
     * @param arrayType The type of the array. This component type of the array must be either exactly the runtime
     *                  type of the element type, or the (un)boxed variant of the runtime type of the element type.
     * @param elementType The element type of the result list type.
     * @param <S> The backing serialized type.
     * @param <E> The (possibly boxed) array component type.
     * @param <A> The array type.
     * @return A {@link ListConfigType} that holds an array of elements of the config type elementType.
     */
    private static <S, E, A> ListConfigType<A, S> makeArray(Class<A> arrayType, ConfigType<E, S, ?> elementType) {
        @SuppressWarnings("unchecked") Class<E> componentType = (Class<E>) arrayType.getComponentType();
        Class<E> boxedComponentType = TypeMagic.wrapPrimitive(componentType);
        // assert that the unchecked cast above is in fact valid
        assert boxedComponentType == TypeMagic.wrapPrimitive(elementType.getRuntimeType()) : "Array component type does not match element type modulo boxing";
        return new ListConfigType<>(
                new ListSerializableType<>(elementType.getSerializedType()),
                arrayType,
                l -> {
                    A arr = arrayType.cast(Array.newInstance(componentType, l.size()));
                    for (int i = 0; i < Array.getLength(arr); i++) {
                        Array.set(arr, i, elementType.toRuntimeType(l.get(i)));
                    }
                    return arr;
                },
                arr -> {
                    List<S> ret = new ArrayList<>(Array.getLength(arr));
                    for (int i = 0; i < Array.getLength(arr); i++) {
                        E e = boxedComponentType.cast(Array.get(arr, i));
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
