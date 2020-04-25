package me.zeroeightsix.fiber.api.schema.type;

import javax.annotation.Nullable;
import java.util.Optional;

public final class TypeCheckResult<V> {
    private static final TypeCheckResult<?> UNRECOVERABLE = new TypeCheckResult<>(false, null);

    public static <V> TypeCheckResult<V> successful(V initialValue) {
        return new TypeCheckResult<>(true, initialValue);
    }

    public static <V> TypeCheckResult<V> failed(V correctedValue) {
        return new TypeCheckResult<>(false, correctedValue);
    }

    public static <V> TypeCheckResult<V> unrecoverable() {
        @SuppressWarnings("unchecked") TypeCheckResult<V> t = (TypeCheckResult<V>) UNRECOVERABLE;
        return t;
    }

    private final boolean passed;
    @Nullable
    private final V correctedValue;

    public TypeCheckResult(boolean passed, @Nullable V correctedValue) {
        this.passed = passed;
        this.correctedValue = correctedValue;
    }

    public boolean hasPassed() {
        return this.passed;
    }

    public Optional<V> getCorrectedValue() {
        return Optional.ofNullable(this.correctedValue);
    }
}
