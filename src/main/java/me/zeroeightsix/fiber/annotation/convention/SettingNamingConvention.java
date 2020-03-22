package me.zeroeightsix.fiber.annotation.convention;

/**
 * A setting naming convention decides how a setting is named based upon the variable it was created from.
 * <br> Java fields are usually {@code lowerCamelCase}, while developers might want their configuration to use {@code snake_case} or {@code lowercase}.
 * Classes implementing this interface are required to make the conversion from {@code lowerCamelCase} to another format.
 *
 * @see UnderscoredLowerCaseConvention
 * @see NoNamingConvention
 */
public interface SettingNamingConvention {

    String name(String name);

}
