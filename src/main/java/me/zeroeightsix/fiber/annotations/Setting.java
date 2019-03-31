package me.zeroeightsix.fiber.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Setting {

    @Retention(RetentionPolicy.RUNTIME)
    @interface Final {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface NoForceFinal {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignored {}

}
