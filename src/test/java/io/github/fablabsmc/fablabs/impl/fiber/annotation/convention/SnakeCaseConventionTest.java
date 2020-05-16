package io.github.fablabsmc.fablabs.impl.fiber.annotation.convention;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SnakeCaseConventionTest {
	private static final String[] STRINGS = {
			"Hello world",
			"helloWorld",
			"HelloWorld",
			"Hello World",
			"Hello_World",
			"hello_world"
	};

	private static final String EXPECTED = "hello_world";

	@Test
	@DisplayName("Name conversions")
	void testName() {
		for (String s : STRINGS) {
			assertEquals(EXPECTED, SettingNamingConvention.SNAKE_CASE.name(s), "Converting " + s + " to " + EXPECTED);
		}
	}
}
