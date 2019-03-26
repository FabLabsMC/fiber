package me.zeroeightsix.fiber.pojo.conventions;

public class NoNamingConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name;
    }

}
