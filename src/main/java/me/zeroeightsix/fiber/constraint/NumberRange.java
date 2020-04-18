package me.zeroeightsix.fiber.constraint;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Objects;

public final class NumberRange {

    public static <N extends Number> NumberRange of(@Nullable N min, @Nullable N max, @Nullable N step) {
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
