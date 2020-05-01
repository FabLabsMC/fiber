package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

/**
 * Capable of being marked with a comment.
 */
public interface Commentable {
	/**
	 * Returns the comment that was assigned to this class.
	 *
	 * @return the comment
	 */
	String getComment();
}
