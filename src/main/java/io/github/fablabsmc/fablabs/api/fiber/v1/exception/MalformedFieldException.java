package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;

/**
 * An exception thrown by {@link AnnotatedSettings AnnotatedSettings} during the conversion of a POJO to a {@link ConfigBranch branch} when a field was not in the expected format.
 */
public class MalformedFieldException extends FiberException {
	public MalformedFieldException(String s) {
		super(s);
	}
}
