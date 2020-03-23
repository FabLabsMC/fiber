package me.zeroeightsix.fiber.exception;

/**
 * An exception thrown by fiber when something goes unrecoverably wrong.
 *
 * <p> Unlike {@link FiberException}, this exception doesn't have to be caught. It happens only with grave mistakes like putting numerical constraints on non-numeric values.
 */
public class RuntimeFiberException extends RuntimeException {

    public RuntimeFiberException(String s) {
        super(s);
    }

    public RuntimeFiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
