package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention;

/**
 * A setting naming convention decides how a setting is named based upon the variable it was created from.
 *
 * <p>Java fields are usually {@code lowerCamelCase}, while developers might want their configuration to use {@code snake_case} or {@code lowercase}.
 * Classes implementing this interface are required to make the conversion from {@code lowerCamelCase} to another format.
 *
 * @see NoNamingConvention
 * @see SnakeCaseConvention
 * @see LowercaseConvention
 */
public interface SettingNamingConvention {
	/**
	 * For the given {@code lowerCamelCase} name, returns a name using the convention
	 * define by this object.
	 *
	 * @param name The name, in lower camel case.
	 * @return The name, formatted according to the convention defined by this object.
	 */
	String name(String name);
}
