package me.zeroeightsix.fiber.exception;

import me.zeroeightsix.fiber.api.exception.FiberException;

public class FiberTypeProcessingException extends FiberException {
    public FiberTypeProcessingException(String message) {
        super(message);
    }

    public FiberTypeProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
