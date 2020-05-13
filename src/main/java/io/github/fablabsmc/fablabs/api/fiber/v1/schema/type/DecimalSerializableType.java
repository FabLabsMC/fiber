package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.DecimalConstraintChecker;

/**
 * The {@link SerializableType} for numeric ranges. This type handles integral as well as real
 * and fractional ranges using Java's {@link BigDecimal} type.
 */
public final class DecimalSerializableType extends SerializableType<BigDecimal> {
	/**
	 * Specifies a numerical lower bound.
	 *
	 * <p>Values must be equal to or greater than the constraint's value to satisfy the constraint.
	 */
	@Nullable
	private final BigDecimal minimum;
	/**
	 * Specifies a numerical upper bound.
	 *
	 * <p>Values must be equal to or lesser than the constraint's value to satisfy the constraint.
	 */
	@Nullable
	private final BigDecimal maximum;
	@Nullable
	private final BigDecimal increment;

	public DecimalSerializableType(@Nullable BigDecimal min, @Nullable BigDecimal max, @Nullable BigDecimal increment) {
		super(BigDecimal.class, DecimalConstraintChecker.instance());

		if (min != null && max != null) {
			if (min.compareTo(max) > 0) {
				throw new IllegalArgumentException("Provided minimum " + min + " is greater than provided maximum " + max);
			}

			if (increment != null && max.subtract(min).compareTo(increment) < 0) {
				throw new IllegalArgumentException("Provided step " + increment + " is bigger than range [" + min + ", " + max + "]");
			}
		}

		if (increment != null) {
			if (min == null) throw new IllegalStateException("A nonnull increment requires a minimum value");

			if (increment.compareTo(BigDecimal.ZERO) <= 0) {
				throw new IllegalArgumentException("Increment cannot be negative (" + increment + ")");
			}
		}

		this.minimum = min;
		this.maximum = max;
		this.increment = increment;
	}

	@Nullable
	public BigDecimal getMinimum() {
		return this.minimum;
	}

	@Nullable
	public BigDecimal getMaximum() {
		return this.maximum;
	}

	@Nullable
	public BigDecimal getIncrement() {
		return this.increment;
	}

	@Override
	public <S> void serialize(TypeSerializer<S> serializer, S target) {
		serializer.serialize(this, target);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		DecimalSerializableType that = (DecimalSerializableType) o;
		return Objects.equals(this.minimum, that.minimum)
				&& Objects.equals(this.maximum, that.maximum)
				&& Objects.equals(this.increment, that.increment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.minimum, this.maximum, this.increment);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", DecimalSerializableType.class.getSimpleName() + "[", "]")
				.add("minimum=" + minimum)
				.add("maximum=" + maximum)
				.add("increment=" + increment)
				.toString();
	}
}
