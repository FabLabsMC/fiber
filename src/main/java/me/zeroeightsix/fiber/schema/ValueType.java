package me.zeroeightsix.fiber.schema;

import java.math.BigDecimal;
import java.util.function.Function;

public final class ValueType<T0, T> {
    public static final ValueType<BigDecimal, Integer> INT = new ValueType<>(PrimitiveValueType.NUMBER, Integer.class, BigDecimal::new, BigDecimal::intValue);
    public static final ValueType<BigDecimal, Long> LONG = new ValueType<>(PrimitiveValueType.NUMBER, Long.class, BigDecimal::new, BigDecimal::longValue);

    private final PrimitiveValueType<T0> baseType;
    private final Class<T> realType;
    private final Function<T, T0> f0;
    private final Function<T0, T> f;

    public ValueType(PrimitiveValueType<T0> baseType, Class<T> realType, Function<T, T0> f0, Function<T0, T> f) {
        this.baseType = baseType;
        this.realType = realType;
        this.f0 = f0;
        this.f = f;
    }

    public T0 toBaseType(T realType) {
        return f0.apply(realType);
    }

    public T toRealType(T0 baseType) {
        return f.apply(baseType);
    }
}
