package me.zeroeightsix.fiber.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Setting {

    @interface Final {}

    @interface NoForceFinal {}

    @interface Ignored {}

    Constraint[] constraints() default {};

}
