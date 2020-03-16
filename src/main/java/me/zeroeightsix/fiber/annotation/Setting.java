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

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface Min {
            double value();
        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface Max {
            double value();
        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface MinStringLength {
            int value();
        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface MaxStringLength {
            int value();
        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface Regex {
            @RegEx String value();
        }
    }
}
