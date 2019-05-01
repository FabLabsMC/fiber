package me.zeroeightsix.fiber.annotation.convention;

public class NoNamingConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name;
    }

}
