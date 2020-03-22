package me.zeroeightsix.fiber.annotation.convention;

public class LowercaseConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name.toLowerCase();
    }

}
