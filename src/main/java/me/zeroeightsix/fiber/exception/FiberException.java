package me.zeroeightsix.fiber.exception;

/**
 * An exception thrown by fiber when something goes unrecoverably wrong. Unlike {@link RuntimeFiberException}, this exception must be caught.
 */
public class FiberException extends Exception {

    public FiberException(String s) {
        super(s);
    }

    public FiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
