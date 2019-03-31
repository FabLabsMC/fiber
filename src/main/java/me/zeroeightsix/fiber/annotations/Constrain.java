package me.zeroeightsix.fiber.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Constrain {

    @Retention(RetentionPolicy.RUNTIME)
    @interface Min {
        double value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Max {
        double value();
    }

}
