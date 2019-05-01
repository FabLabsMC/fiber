package me.zeroeightsix.fiber.annotation.exception;

import me.zeroeightsix.fiber.exception.FiberException;

public class MalformedConstructorException extends FiberException {
    public MalformedConstructorException(String s) {
        super(s);
    }

    public MalformedConstructorException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
