package me.zeroeightsix.fiber.api.annotation.convention;

/**
 * A naming convention that converts java-styled {@code lowerCamelCase} names and {@code Proper case} names to {@code snake_case}.
 *
 * @see SnakeCaseConvention#name(String)
 */
public class SnakeCaseConvention implements SettingNamingConvention {

    @Override
    public String name(String name) {
        return name.replaceAll("(?!^)[ _]*([A-Z])", "_$1").toLowerCase().replace(' ', '_');
    }

}
