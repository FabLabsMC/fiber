package me.zeroeightsix.fiber.annotation.exception;

import me.zeroeightsix.fiber.exception.FiberException;

public class MalformedFieldException extends FiberException {
    public MalformedFieldException(String s) {
        super(s);
    }

    public MalformedFieldException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
