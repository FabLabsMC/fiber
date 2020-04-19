package me.zeroeightsix.fiber.api.exception;

import me.zeroeightsix.fiber.api.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;

/**
 * An exception thrown by {@link AnnotatedSettings AnnotatedSettings} during the conversion of a POJO to a {@link ConfigBranch branch} when a field was not in the expected format.
 */
public class MalformedFieldException extends FiberException {
    public MalformedFieldException(String s) {
        super(s);
    }
}
