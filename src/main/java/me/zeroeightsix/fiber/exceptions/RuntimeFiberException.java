package me.zeroeightsix.fiber.exceptions;

public class RuntimeFiberException extends RuntimeException {

    public RuntimeFiberException(String s) {
        super(s);
    }

    public RuntimeFiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
