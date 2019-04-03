package me.zeroeightsix.fiber.annotations.exceptions;

import me.zeroeightsix.fiber.exceptions.FiberException;

public class MalformedFieldException extends FiberException {
    public MalformedFieldException(String s) {
        super(s);
    }

    public MalformedFieldException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
