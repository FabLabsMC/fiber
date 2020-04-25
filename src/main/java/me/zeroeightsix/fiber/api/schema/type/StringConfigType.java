package me.zeroeightsix.fiber.api.schema.type;

import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.StringTypeChecker;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Pattern;

public final class StringConfigType extends ConfigType<String> {
    public static final StringConfigType DEFAULT_STRING = new StringConfigType(0, Integer.MAX_VALUE, null);

    private final int minLength;
    private final int maxLength;
    @Nullable
    private final Pattern pattern;
    private final StringTypeChecker constraint;

    public StringConfigType(int minLength, int maxLength, @Nullable Pattern pattern) {
        super(String.class);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
        this.constraint = new StringTypeChecker(this);
    }

    /**
     * Specifies a minimum string length.
     *
     * <p> Values must be of equal or longer length than the returned value to satisfy the constraint.
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
     * <p> Values must be of equal or shorter length than the returned value to satisfy the constraint.
     * For example: if the max length is 3.
     * <ul>
     *     <li> {@code "AB"} and {@code "ABC} would satisfy the constraint</li>
     *     <li>{@code "ABCD"} would not satisfy the constraint</li>
     * </ul>
     */
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Specifies a pattern that must match.
     *
     * <p> Values must match the constraint's value, which is a regular expression (regex).
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
    protected StringTypeChecker getConstraint() {
        return this.constraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        StringConfigType that = (StringConfigType) o;
        return this.minLength == that.minLength &&
                this.maxLength == that.maxLength &&
                Objects.equals(this.pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minLength, this.maxLength, this.pattern);
    }
}
