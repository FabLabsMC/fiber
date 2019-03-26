package me.zeroeightsix.fiber.pojo;

import me.zeroeightsix.fiber.pojo.conventions.NoNamingConvention;
import me.zeroeightsix.fiber.pojo.conventions.SettingNamingConvention;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Settings {

    boolean noForceFinals() default false;

    Class<? extends SettingNamingConvention> namingConvention() default NoNamingConvention.class;

}
