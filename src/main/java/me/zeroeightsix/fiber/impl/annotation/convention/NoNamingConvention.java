package me.zeroeightsix.fiber.impl.annotation.convention;

import me.zeroeightsix.fiber.api.annotation.convention.SettingNamingConvention;

/**
 * A naming convention that does not modify any names.
 */
public class NoNamingConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name;
    }

}
