package me.zeroeightsix.fiber.annotation;

import javax.annotation.RegEx;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field is a setting, and must be serialised as such.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {

    /**
     * Marks a setting as constant or final.
     * <br> Constant settings can not have their value changed after being initialised.
     * @return whether or not this setting is constant
     */
    boolean constant() default false;

    /**
     * Sets the name that will be used for this setting.
     * <br> Custom names have a higher authority than {@link me.zeroeightsix.fiber.annotation.convention.SettingNamingConvention naming conventions}, and will not be affected by them.
     * If the name is empty, the name of the field being annotated will be used and the naming convention will be applied to that name.
     *
     * @return An empty string ({@code ""}) if no custom name was set, or the custom name if one was set.
     */
    String name() default "";

    /**
     * Sets the comment that will be used for this setting.
     * <br> If empty, no comment will be set.
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
     * Indicates that this setting is a node.
     *
     * @see me.zeroeightsix.fiber.tree.Node
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Node {
        /**
         * Sets the name that will be used for this node.
         * <br> Custom names have a higher authority than {@link me.zeroeightsix.fiber.annotation.convention.SettingNamingConvention naming conventions}, and will not be affected by them.
         * If the name is empty, the name of the field being annotated will be used and the naming convention will be applied to that name.
         *
         * @return An empty string ({@code ""}) if no custom name was set, or the custom name if one was set.
         */
        String name() default "";
    }

    @Target({})
    @interface Constrain {

        /**
         * Indicates that this value is limited to being equal to or bigger than a number.
         *
         * @see SmallerThan SmallerThan
         */
        @Target({ElementType.TYPE_USE})
        @Retention(RetentionPolicy.RUNTIME)
        @interface BiggerThan {
            /**
             * The minimum value allowed.
             * <br> Settings being constrained using this annotation must be equal to or bigger than this {@code value}.
             *
             * @return the minimum value
             */
            double value();
        }

        /**
         * Indicates that this value is limited to being equal to or smaller than a number.
         *
         * @see BiggerThan BiggerThan
         */
        @Target({ElementType.TYPE_USE})
        @Retention(RetentionPolicy.RUNTIME)
        @interface SmallerThan {
            /**
             * The maximum value allowed.
             * <br> Settings being constrained using this annotation must be equal to or smaller than this {@code value}.
             *
             * @return the maximum value
             */
            double value();
        }

        /**
         * Indicates that this value's length or size is limited to being equal to or larger than a number.
         * <br> For example, a string annotated with {@code MinLength(5)} must be 5 or more characters long. {@code "ABCD"} would not be allowed, but {@code "ABCDE"} would be.
         *
         * @see MaxLength MaxLength
         */
        @Target({ElementType.TYPE_USE})
        @Retention(RetentionPolicy.RUNTIME)
        @interface MinLength {
            /**
             * The minimum length allowed.
             *
             * @see MinLength MinLength
             */
            int value();
        }

        /**
         * Indicates that this value's length or size is limited to being equal to or smaller than a number.
         * <br> For example, a string annotated with {@code MaxLength(5)} must be 5 or less characters long. {@code "ABCDE"} would be allowed, but {@code "ABCDEF"} would not be.
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
