package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.annotation.convention.NoNamingConvention;
import me.zeroeightsix.fiber.annotation.convention.SettingNamingConvention;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a type represents a structure in a configuration file.
 *
 * <p> While it not necessary to use this annotation to serialise a POJO into a {@link Setting.Group Node}, it can be used to specify other metadata.
 *
 * @see Settings#onlyAnnotated()
 * @see Settings#namingConvention()
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Settings {

    /**
     * Specifies whether or not all fields in this class should be serialised, or only those annotated with {@link Setting}.
     *
     * <p> Note that if you want to exclude one field without having to mark all others with the {@link Setting} annotation, the field can be marked as {@code transient} instead.
     * All transient fields are ignored by default.
     *
     * @return whether or not only annotated fields should be serialised
     */
    boolean onlyAnnotated() default false;

    /**
     * Returns the naming convention used for (re)naming the fields in this class during serialisation.
     *
     * @return the {@link SettingNamingConvention naming convention} for this class
     */
    Class<? extends SettingNamingConvention> namingConvention() default NoNamingConvention.class;

}
