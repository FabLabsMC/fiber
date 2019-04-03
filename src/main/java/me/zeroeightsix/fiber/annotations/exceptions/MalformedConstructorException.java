package me.zeroeightsix.fiber.annotations.exceptions;

public class MalformedConstructorException extends FiberException {
    public MalformedConstructorException(String s) {
        super(s);
    }

    public MalformedConstructorException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
