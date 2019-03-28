package me.zeroeightsix.fiber.annotations.conventions;

public class NoNamingConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name;
    }

}
