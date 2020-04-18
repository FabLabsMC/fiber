package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.tree.ConfigLeaf;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Specifies a condition values must satisfy before being set to a {@code ConfigLeaf}.
 *
 * @param <V> the type of values this constraint checks
 * @see ConfigLeaf
 */
public abstract class Constraint<V> {

	private final ConstraintType type;

	public Constraint(ConstraintType type) {
		this.type = type;
	}

	public ConstraintType getType() {
		return type;
	}

	/**
	 * Tests a value against this {@code Constraint}.
	 *
	 * <p> This method may provide a corrected value that can be used
	 * if the input value is invalid.
	 *
	 * @param value the value
	 * @return {@code true} if {@code value} satisfies the constraint
	 */
	public abstract TestResult<V> test(V value);

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	public static final class TestResult<V> {
		private static final TestResult<?> UNRECOVERABLE = new TestResult<>(false, null);

		public static <V> TestResult<V> successful(V initialValue) {
			return new TestResult<>(true, initialValue);
		}

		public static <V> TestResult<V> failed(V correctedValue) {
			return new TestResult<>(false, correctedValue);
		}

		public static <V> TestResult<V> unrecoverable() {
			@SuppressWarnings("unchecked") TestResult<V> t = (TestResult<V>) UNRECOVERABLE;
			return t;
		}

		private final boolean passed;
		@Nullable
		private final V correctedValue;

		public TestResult(boolean passed, @Nullable V correctedValue) {
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
}
