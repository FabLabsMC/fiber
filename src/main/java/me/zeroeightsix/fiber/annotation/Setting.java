package me.zeroeightsix.fiber.annotation;

import javax.annotation.RegEx;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {

    boolean constant() default false;
    String name() default "";
    String comment() default "";

    boolean ignore() default false;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Node {
        String name() default "";
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
             * <p> Settings being constrained using this annotation must be equal to or bigger than this value.
             *
             * @return the minimum value
             */
            double min() default Double.NEGATIVE_INFINITY;

            /**
             * The maximum value allowed.
             *
             * <p> Settings being constrained using this annotation must be equal to or
             * smaller than this {@code value}.
             *
             * @return the maximum value
             */
            double max() default Double.POSITIVE_INFINITY;
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
        }

        @Target({ElementType.TYPE_USE})
        @Retention(RetentionPolicy.RUNTIME)
        @interface MinLength {
            int value();
        }

        @Target({ElementType.TYPE_USE})
        @Retention(RetentionPolicy.RUNTIME)
        @interface MaxLength {
            int value();
        }

        @Target({ElementType.TYPE_USE})
        @Retention(RetentionPolicy.RUNTIME)
        @interface Regex {
            @RegEx String value();
        }
    }
}
