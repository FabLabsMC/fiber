package me.zeroeightsix.fiber;

public class RuntimeFiberException extends RuntimeException {

    public RuntimeFiberException(String s) {
        super(s);
    }

    public RuntimeFiberException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
