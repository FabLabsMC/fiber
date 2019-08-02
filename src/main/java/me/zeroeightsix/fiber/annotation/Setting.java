package me.zeroeightsix.fiber.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Setting {

    boolean constant() default false;
    boolean noForceFinal() default false;
    String name() default "";
    String comment() default "";

    boolean ignore() default false;
    @Retention(RetentionPolicy.RUNTIME)
    @interface Node {
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Constrain {

        @Retention(RetentionPolicy.RUNTIME)
        @interface Min {
            double value();
        }

        @Retention(RetentionPolicy.RUNTIME)
        @interface Max {
            double value();
        }

    }
}
