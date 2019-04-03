package me.zeroeightsix.fiber.annotations.conventions;

public class UnderscoredLowerCaseConvention implements SettingNamingConvention {

    /**
     * Attempts to convert java-styled names or spaced names to underscored, lowercase names (C style).
     * For example, "helloWorld" or "Hello world" become "hello_world"
     * @param name  The name to convert to underscored lower case
     * @return      The converted name
     */
    @Override
    public String name(String name) {
        return name.replaceAll("(?!^)[ _]*([A-Z])", "_$1").toLowerCase().replace(' ', '_');
    }
}
