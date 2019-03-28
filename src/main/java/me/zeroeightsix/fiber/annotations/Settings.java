package me.zeroeightsix.fiber.annotations;

import me.zeroeightsix.fiber.annotations.conventions.NoNamingConvention;
import me.zeroeightsix.fiber.annotations.conventions.SettingNamingConvention;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Settings {

    boolean noForceFinals() default false;

    Class<? extends SettingNamingConvention> namingConvention() default NoNamingConvention.class;

}
