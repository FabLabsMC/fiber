package me.zeroeightsix.fiber.annotation.exception;

import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.tree.ConfigGroup;

/**
 * An exception thrown by {@link me.zeroeightsix.fiber.annotation.AnnotatedSettings AnnotatedSettings} during the conversion of a POJO to a {@link ConfigGroup group} when a field was not in the expected format.
 */
public class MalformedFieldException extends FiberException {
    public MalformedFieldException(String s) {
        super(s);
    }
}
