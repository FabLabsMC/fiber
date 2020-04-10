package me.zeroeightsix.fiber.annotation.exception;

import me.zeroeightsix.fiber.api.exception.FiberException;

/**
 * An exception thrown by {@link me.zeroeightsix.fiber.api.annotation.AnnotatedSettings AnnotatedSettings}
 * during the conversion of a POJO to a {@link me.zeroeightsix.fiber.tree.ConfigTree ConfigTree} when a
 * field was not in the expected format.
 */
public class MalformedFieldException extends FiberException {
    public MalformedFieldException(String s) {
        super(s);
    }
}
