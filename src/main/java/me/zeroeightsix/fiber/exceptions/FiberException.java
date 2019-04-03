package me.zeroeightsix.fiber.exceptions;

public class FiberException extends Exception {

    public FiberException(String s) {
        super(s);
    }

    public FiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
