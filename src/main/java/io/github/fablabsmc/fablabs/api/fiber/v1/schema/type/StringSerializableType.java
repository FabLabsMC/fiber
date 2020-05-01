package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.TypeSerializer;
import io.github.fablabsmc.fablabs.impl.fiber.constraint.StringConstraintChecker;

public final class StringSerializableType extends SerializableType<String> {
	public static final StringSerializableType DEFAULT_STRING = new StringSerializableType(0, Integer.MAX_VALUE, null);

	private final int minLength;
	private final int maxLength;
	@Nullable
	private final Pattern pattern;

	public StringSerializableType(int minLength, int maxLength, @Nullable Pattern pattern) {
		super(String.class, StringConstraintChecker.instance());
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.pattern = pattern;
	}

	/**
	 * Specifies a minimum string length.
	 *
	 * <p>Values must be of equal or longer length than the returned value to satisfy the constraint.
	 * For example: if the min length is 3.
	 * <ul>
	 *     <li> {@code "AB"} would not satisfy the constraint</li>
	 *     <li>{@code "ABC"} and {@code "ABCD"} would satisfy the constraint</li>
	 * </ul>
	 */
	public int getMinLength() {
		return this.minLength;
	}

	/**
	 * Specifies a maximum string length.
	 *
	 * <p>Values must be of equal or shorter length than the returned value to satisfy the constraint.
	 * For example: if the max length is 3.
	 * <ul>
	 *     <li> {@code "AB"} and {@code "ABC"} would satisfy the constraint</li>
	 *     <li> {@code "ABCD"} would not satisfy the constraint</li>
	 * </ul>
	 */
	public int getMaxLength() {
		return this.maxLength;
	}

	/**
	 * Specifies a pattern that must match.
	 *
	 * <p>Values must match the constraint's value, which is a regular expression (regex).
	 */
	@Nullable
	public Pattern getPattern() {
		return this.pattern;
	}

	@Override
	public <S> void serialize(TypeSerializer<S> serializer, S target) {
		serializer.serialize(this, target);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		StringSerializableType that = (StringSerializableType) o;
		return this.minLength == that.minLength
				&& this.maxLength == that.maxLength
				&& Objects.equals(this.pattern, that.pattern);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.minLength, this.maxLength, this.pattern);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", StringSerializableType.class.getSimpleName() + "[", "]")
				.add("minLength=" + this.minLength)
				.add("maxLength=" + this.maxLength)
				.add("pattern=" + this.pattern)
				.toString();
	}
}
