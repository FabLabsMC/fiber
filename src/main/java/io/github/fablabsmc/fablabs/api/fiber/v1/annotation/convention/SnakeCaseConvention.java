package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;

/**
 * A naming convention that converts java-styled {@code lowerCamelCase} names and {@code Proper case} names to {@code snake_case}.
 *
 * @see SnakeCaseConvention#name(String)
 * @deprecated use {@link SettingNamingConvention#SNAKE_CASE}.
 */
public class SnakeCaseConvention implements SettingNamingConvention {
	@Override
	public String name(String name) {
		return name.replaceAll("(?!^)[ _]*([A-Z])", "_$1").toLowerCase().replace(' ', '_');
	}
}
