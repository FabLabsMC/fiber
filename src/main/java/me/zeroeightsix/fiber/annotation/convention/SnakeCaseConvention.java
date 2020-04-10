package me.zeroeightsix.fiber.annotation.convention;

/**
 * A naming convention that converts java-styled {@code lowerCamelCase} names and {@code Proper case} names to {@code snake_case}.
 *
 * @see SnakeCaseConvention#name(String)
 */
public class SnakeCaseConvention implements SettingNamingConvention {

    /**
     * Attempts to convert java-styled names or spaced names to underscored, lowercase names (C style).
     *
     * <p> For example, "helloWorld" or "Hello world" become "hello_world"
     *
     * @param name  The name to convert to underscored lower case
     * @return      The converted name
     */
    @Override
    public String name(String name) {
        return name.replaceAll("(?!^)[ _]*([A-Z])", "_$1").toLowerCase().replace(' ', '_');
    }

}
