package me.zeroeightsix.fiber.annotation.convention;

/**
 * A naming convention that converts any input name to lower case.
 */
public class LowercaseConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name.toLowerCase();
    }

}
