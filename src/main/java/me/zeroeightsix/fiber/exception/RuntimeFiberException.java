package me.zeroeightsix.fiber.exception;

/**
 * An exception thrown by fiber when something goes unrecoverably wrong.
 * <br> Unlike {@link FiberException}, this exception doesn't have to be caught.
 */
public class RuntimeFiberException extends RuntimeException {

    public RuntimeFiberException(String s) {
        super(s);
    }

    public RuntimeFiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
