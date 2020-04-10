package me.zeroeightsix.fiber.api.annotation.convention;

import me.zeroeightsix.fiber.impl.annotation.convention.LowercaseConvention;
import me.zeroeightsix.fiber.impl.annotation.convention.NoNamingConvention;
import me.zeroeightsix.fiber.impl.annotation.convention.SnakeCaseConvention;

/**
 * A setting naming convention decides how a setting is named based upon the variable it was created from.
 *
 * <p> Java fields are usually {@code lowerCamelCase}, while developers might want their configuration to use {@code snake_case} or {@code lowercase}.
 * Classes implementing this interface are required to make the conversion from {@code lowerCamelCase} to another format.
 *
 * @see SnakeCaseConvention
 * @see NoNamingConvention
 */
public interface SettingNamingConvention {

    Class<? extends SettingNamingConvention> LOWER_CASE = LowercaseConvention.class;
    Class<? extends SettingNamingConvention> NONE = NoNamingConvention.class;
    Class<? extends SettingNamingConvention> SNAKE_CASE = SnakeCaseConvention.class;

    String name(String name);

}
