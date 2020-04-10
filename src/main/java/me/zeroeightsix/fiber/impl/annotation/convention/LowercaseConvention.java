package me.zeroeightsix.fiber.impl.annotation.convention;

import me.zeroeightsix.fiber.api.annotation.convention.SettingNamingConvention;

/**
 * A naming convention that converts any input name to lower case.
 */
public class LowercaseConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name.toLowerCase();
    }

}
