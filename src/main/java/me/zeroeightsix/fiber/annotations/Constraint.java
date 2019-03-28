package me.zeroeightsix.fiber.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Constraint {

    @interface Min {
        double min();
    }

    @interface Max {
        double max();
    }

}
