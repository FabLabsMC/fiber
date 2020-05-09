package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;

/**
 * @deprecated use {@link SettingNamingConvention#LOWERCASE}.
 */
public class LowercaseConvention implements SettingNamingConvention {
	@Override
	public String name(String name) {
		return name.toLowerCase();
	}
}
