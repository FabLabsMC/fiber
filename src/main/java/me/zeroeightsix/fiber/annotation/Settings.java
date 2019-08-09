package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.annotation.convention.NoNamingConvention;
import me.zeroeightsix.fiber.annotation.convention.SettingNamingConvention;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Settings {

    boolean onlyAnnotated() default false;

    Class<? extends SettingNamingConvention> namingConvention() default NoNamingConvention.class;

}
