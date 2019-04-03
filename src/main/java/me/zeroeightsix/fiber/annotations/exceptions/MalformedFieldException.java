package me.zeroeightsix.fiber.annotations.exceptions;

public class MalformedFieldException extends FiberException {
    public MalformedFieldException(String s) {
        super(s);
    }

    public MalformedFieldException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
