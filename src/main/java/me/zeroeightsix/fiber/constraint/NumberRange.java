package me.zeroeightsix.fiber.constraint;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Objects;

public final class NumberRange {
    public static final NumberRange UNBOUNDED = new NumberRange(null, null, null);

    public static <N extends Number> NumberRange of(@Nullable N min, @Nullable N max, @Nullable N step) {
        if (min == null && max == null && step == null) return UNBOUNDED;
        return new NumberRange(
                min == null ? null : new BigDecimal(min.toString()),
                max == null ? null : new BigDecimal(max.toString()),
                step == null ? null : new BigDecimal(step.toString())
        );
    }

    @Nullable public final BigDecimal min;
    @Nullable public final BigDecimal max;
    @Nullable public final BigDecimal step;

    public NumberRange(@Nullable BigDecimal min, @Nullable BigDecimal max, @Nullable BigDecimal step) {
        if (min != null && max != null) {
            if (min.compareTo(max) > 0) throw new IllegalArgumentException("Provided minimum " + min + " is greater than provided maximum " + max);
            if (step != null && max.subtract(min).compareTo(step) < 0) throw new IllegalArgumentException("Provided step " + step + " is bigger than range [" + min + ", " + max + "]");
        }
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public boolean contains(NumberRange other) {
        return (this.min == null || other.min != null && this.min.compareTo(other.min) <= 0) &&
                (this.max == null || other.max != null && this.max.compareTo(other.max) >= 0) &&
                (this.step == null || other.step != null && other.step.remainder(this.step).intValue() == 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberRange that = (NumberRange) o;
        return Objects.equals(min, that.min) &&
                Objects.equals(max, that.max) &&
                Objects.equals(step, that.step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, step);
    }
}
