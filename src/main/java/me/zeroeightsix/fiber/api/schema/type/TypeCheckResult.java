package me.zeroeightsix.fiber.api.schema.type;

import java.util.Optional;

import javax.annotation.Nullable;

/**
 * The result of type checking a serialized value.
 *
 * @param <V> the actual type of the tested value
 * @see SerializableType#test(Object)
 */
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

	private TypeCheckResult(boolean passed, @Nullable V correctedValue) {
		this.passed = passed;
		this.correctedValue = correctedValue;
	}

	/**
	 * Returns {@code true} if the tested value passed the type check.
	 *
	 * <p>A value passes if it matches every attribute set on
	 * the {@link SerializableType}. If at least one constraint check failed,
	 * this method returns {@code false}.
	 *
	 * @return {@code true} if the test passed, {@code false} otherwise.
	 */
	public boolean hasPassed() {
		return this.passed;
	}

	/**
	 * Returns a possible corrected value based on the tested value.
	 *
	 * <p>If the test passes, this method returns an {@code Optional} describing
	 * the tested value.
	 *
	 * @return an {@code Optional} describing a possible corrected value,
	 * or an empty {@code Optional} if the test was unrecoverable.
	 */
	public Optional<V> getCorrectedValue() {
		return Optional.ofNullable(this.correctedValue);
	}
}
