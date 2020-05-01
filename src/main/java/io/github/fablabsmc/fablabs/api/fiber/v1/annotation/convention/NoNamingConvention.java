package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention;

/**
 * A naming convention that does not modify any names.
 */
public class NoNamingConvention implements SettingNamingConvention {
	@Override
	public String name(String name) {
		return name;
	}
}
