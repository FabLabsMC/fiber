package me.zeroeightsix.fiber.exception;

public class RuntimeFiberException extends RuntimeException {

    public RuntimeFiberException(String s) {
        super(s);
    }

    public RuntimeFiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
