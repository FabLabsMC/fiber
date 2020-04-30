package me.zeroeightsix.fiber.impl.annotation.convention;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.zeroeightsix.fiber.api.annotation.convention.NoNamingConvention;
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
