package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;

/**
 * A naming convention that does not modify any names.
 * @deprecated use {@link SettingNamingConvention#NONE}
 */
public class NoNamingConvention implements SettingNamingConvention {
	@Override
	public String name(String name) {
		return name;
	}
}
