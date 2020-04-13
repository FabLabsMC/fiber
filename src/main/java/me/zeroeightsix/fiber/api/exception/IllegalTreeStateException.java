package me.zeroeightsix.fiber.api.exception;

import me.zeroeightsix.fiber.api.tree.ConfigTree;

/**
 * Signals that a {@link ConfigTree} is not in an appropriate state for
 * the requested operation.
 */
public class IllegalTreeStateException extends IllegalStateException {
    public IllegalTreeStateException(String message) {
        super(message);
    }
}
