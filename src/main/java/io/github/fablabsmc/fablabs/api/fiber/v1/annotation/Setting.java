package io.github.fablabsmc.fablabs.api.fiber.v1.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.RegEx;

import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigAttribute;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;

/**
 * Marks a field as a setting. This annotation is optional unless the class has been annotated with {@code @Settings(onlyAnnotated = true)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {
	/**
	 * Marks a setting as constant or final.
	 *
	 * <p>Constant settings can not have their value changed after being initialised.
	 *
	 * @return whether or not this setting is constant
	 * @deprecated constants should now be stored as schema {@link ConfigAttribute}s,
	 * not as values in an editable config tree.
	 */
	@Deprecated boolean constant() default false;

	/**
	 * Sets the name that will be used for this setting.
	 *
	 * <p>Custom names have a higher authority than {@link SettingNamingConvention naming conventions}, and will not be affected by them.
	 * If the name is empty, the name of the field being annotated will be used and the naming convention will be applied to that name.
	 *
	 * @return An empty string ({@code ""}) if no custom name was set, or the custom name if one was set.
	 */
	String name() default "";

	/**
	 * Sets the comment that will be used for this setting.
	 *
	 * <p>If empty, no comment will be set.
	 *
	 * @return An empty string ({@code ""}) if no comment was set, or the comment if one was set.
	 */
	String comment() default "";

	/**
	 * Marks that this field should be ignored during serialisation.
	 *
	 * @return Whether or not this field should be ignored
	 */
	boolean ignore() default false;

	/**
	 * Indicates that this setting represents a group of settings, rather than a single value.
	 *
	 * @see ConfigBranch
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Group {
		/**
		 * Sets the name that will be used for this node.
		 *
		 * <p>Custom names have a higher authority than {@link SettingNamingConvention naming conventions}, and will not be affected by them.
		 * If the name is empty, the name of the field being annotated will be used and the naming convention will be applied to that name.
		 *
		 * @return An empty string ({@code ""}) if no custom name was set, or the custom name if one was set.
		 */
		String name() default "";

		/**
		 * Sets the comment that will be used for this setting group.
		 *
		 * <p>If empty, no comment will be set.
		 *
		 * @return An empty string ({@code ""}) if no comment was set, or the comment if one was set.
		 */
		String comment() default "";
	}

	@Target({})
	@interface Constrain {
		/**
		 * Indicates that this value is limited to a range of numerical values.
		 *
		 * @see BigRange
		 */
		@Target({ElementType.TYPE_USE})
		@Retention(RetentionPolicy.RUNTIME)
		@interface Range {
			/**
			 * The minimum value allowed (inclusive).
			 *
			 * <p>Settings being constrained using this annotation must be equal to or greater than this value.
			 *
			 * @return the minimum value
			 */
			double min() default Double.NEGATIVE_INFINITY;

			/**
			 * The maximum value allowed (inclusive).
			 *
			 * <p>Settings being constrained using this annotation must be equal to or
			 * less than this {@code value}.
			 *
			 * @return the maximum value
			 */
			double max() default Double.POSITIVE_INFINITY;

			/**
			 * The minimum allowed distance between adjacent valid values. Must be positive.
			 *
			 * <p>Settings being constrained using this annotation must be a whole
			 * multiple of the step size greater than the minimum value.
			 *
			 * @return the step size
			 */
			double step() default Double.MIN_VALUE;
		}

		/**
		 * Indicates that this value is limited to a range of numerical values with unbounded precision.
		 *
		 * @see Range
		 * @see java.math.BigDecimal
		 */
		@Target({ElementType.TYPE_USE})
		@Retention(RetentionPolicy.RUNTIME)
		@interface BigRange {
			/**
			 * The minimum value for this field (inclusive). If left empty, there will be no minimum.
			 *
			 * @return the minimum value
			 */
			String min() default "";

			/**
			 * The maximum value for this field (inclusive). If left empty, there will be no maximum.
			 *
			 * @return the maximum value
			 */
			String max() default "";

			/**
			 * The minimum allowed distance between adjacent valid values. If empty,
			 * equivalent to a step size of zero, otherwise the value must represent
			 * a positive real number.
			 *
			 * <p>Settings being constrained using this annotation must be a whole
			 * multiple of the step size greater than the minimum value.
			 *
			 * @return the step size
			 */
			String step() default "";
		}

		/**
		 * Indicates that this value's length or size is limited to being equal to or larger than a number.
		 *
		 * <p>For example, a string annotated with {@code MinLength(5)} must be 5 or more characters long. {@code "ABCD"} would not be allowed, but {@code "ABCDE"} would be.
		 *
		 * @see MaxLength MaxLength
		 */
		@Target({ElementType.TYPE_USE})
		@Retention(RetentionPolicy.RUNTIME)
		@interface MinLength {
			/**
			 * Returns the minimum length allowed.
			 *
			 * @return the minimum length allowed
			 * @see MinLength MinLength
			 */
			int value();
		}

		/**
		 * Indicates that this value's length or size is limited to being equal to or smaller than a number.
		 *
		 * <p>For example, a string annotated with {@code MaxLength(5)} must be 5 or less characters long. {@code "ABCDE"} would be allowed, but {@code "ABCDEF"} would not be.
		 *
		 * @see MinLength MinLength
		 */
		@Target({ElementType.TYPE_USE})
		@Retention(RetentionPolicy.RUNTIME)
		@interface MaxLength {
			int value();
		}

		/**
		 * Indicates that this value's string representation must match a certain regex.
		 */
		@Target({ElementType.TYPE_USE})
		@Retention(RetentionPolicy.RUNTIME)
		@interface Regex {
			/**
			 * The regex this value must match.
			 *
			 * @return the regex
			 * @see Regex Regex
			 */
			@RegEx String value();
		}
	}
}
