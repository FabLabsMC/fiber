package me.zeroeightsix.fiber.schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class PrimitiveValueType<T> {
    public static final PrimitiveValueType<String> STRING = new PrimitiveValueType<>(String.class);
    public static final PrimitiveValueType<BigDecimal> NUMBER = new PrimitiveValueType<>(BigDecimal.class);
    public static final PrimitiveValueType<List<?>> LIST = new PrimitiveValueType<>(List.class);
    public static final PrimitiveValueType<Map<?, ?>> RECORD = new PrimitiveValueType<>(Map.class);

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    private PrimitiveValueType(Class<? super T> type) {
        this.type = (Class<T>) type;
    }
}
