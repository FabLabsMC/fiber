package me.zeroeightsix.fiber.impl.annotation.convention;

import me.zeroeightsix.fiber.impl.annotation.convention.NoNamingConvention;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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