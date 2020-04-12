package me.zeroeightsix.fiber.exception;

/**
 * Signals that a {@link me.zeroeightsix.fiber.tree.ConfigTree} is not in an appropriate state for
 * the requested operation.
 */
public class IllegalTreeStateException extends IllegalStateException {
    public IllegalTreeStateException(String message) {
        super(message);
    }
}
