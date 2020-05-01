package io.github.fablabsmc.fablabs.impl.fiber.annotation.convention;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.NoNamingConvention;
import org.junit.jupiter.api.Test;

class NoNamingConventionTest extends NoNamingConvention {
	private static String[] STRINGS = {
			"anything",
			"you",
			"put",
			"here",
			"will",
			"just",
			"stay",
			"the",
			"same",
			"."
	};

	@Test
	void name() {
		for (String string : STRINGS) {
			assertEquals(string, super.name(string));
		}
	}
}
