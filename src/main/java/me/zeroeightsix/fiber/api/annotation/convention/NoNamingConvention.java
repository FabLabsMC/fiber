package me.zeroeightsix.fiber.api.annotation.convention;

/**
 * A naming convention that does not modify any names.
 */
public class NoNamingConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name;
    }

}
