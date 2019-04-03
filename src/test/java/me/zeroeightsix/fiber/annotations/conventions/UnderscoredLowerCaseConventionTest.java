package me.zeroeightsix.fiber.annotations.conventions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnderscoredLowerCaseConventionTest extends UnderscoredLowerCaseConvention {

    @Test
    void testName() {
        assertEquals("hello_world", name("Hello world"), "Hello world");
        assertEquals("hello_world", name("helloWorld"), "helloWorld");
        assertEquals("hello_world", name("HelloWorld"), "HelloWorld");
        assertEquals("hello_world", name("Hello World"), "Hello World");
        assertEquals("hello_world", name("Hello_World"), "Hello_World");
        assertEquals("hello_world", name("hello_world"), "hello_world");
    }

}