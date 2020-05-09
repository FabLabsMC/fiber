package io.github.fablabsmc.fablabs.api.fiber.v1.annotation;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.LowercaseConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.NoNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.SnakeCaseConvention;

/**
 * A setting naming convention decides how a setting is named based upon the variable it was created from.
 *
 * <p>Java fields are usually {@code lowerCamelCase}, while developers might want their configuration to use {@code snake_case} or {@code lowercase}.
 * Classes implementing this interface are required to make the conversion from {@code lowerCamelCase} to another format.
 */
@FunctionalInterface
public interface SettingNamingConvention {
	/**
	 * A naming convention that converts all characters to lowercase.
	 */
	SettingNamingConvention LOWERCASE = new LowercaseConvention();

	/**
	 * A naming convention that does not modify any names.
	 */
	SettingNamingConvention NONE = new NoNamingConvention();

	/**
	 * A naming convention that converts java-styled {@code lowerCamelCase} names and {@code Proper case} names to {@code snake_case}.
	 */
	SettingNamingConvention SNAKE_CASE = new SnakeCaseConvention();

	/**
	 * For the given {@code lowerCamelCase} name, returns a name using the convention
	 * defined by this object.
	 *
	 * @param name The name, in lower camel case.
	 * @return The name, formatted according to the convention defined by this object.
	 */
	String name(String name);
}
