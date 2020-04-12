package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.DuplicateChildException;
import me.zeroeightsix.fiber.exception.IllegalTreeStateException;

import javax.annotation.Nullable;

/**
 * The building block of a tree: every node implement this interface.
 */
public interface ConfigNode {

    /**
     * Returns this node's name.
     *
     * @return this node's name
     */
    String getName();

    /**
     * Returns the meta-configuration tree that describes the attributes of this node.
     *
     * <p> Attributes store metadata pertaining to the node itself, rather than its value.
     * The returned tree can be mutated by third parties to supplement the default node metadata.
     * Examples of attributes include translation keys or rendering information.
     *
     * <p> As the returned data structure is shared by every attribute source,
     * attributes should be grouped by namespace.
     *
     * @return this node's configurable attributes
     */
    AttributeTree getAttributes();

    /**
     * Returns this node's parent, if any.
     *
     * @return this node's parent, or {@code null} if it is not part of a config tree
     */
    @Nullable
    ConfigBranch getParent();

    /**
     * Attaches this node to an existing branch.
     *
     * <p> After this method has returned normally, this node will be part
     * of the branch's {@linkplain ConfigBranch#getItems() children}, and this
     * node's parent will be set to {@code parent}.
     *
     * <p> If {@code parent} is {@code null}, this method does not mutate any state.
     * It will however still throw {@code IllegalTreeStateException} if this node
     * is not in a suitable state to be attached to another parent. To detach the node
     * from its current parent, use {@link #detach()}.
     *
     * @param parent the new parent branch for this node
     * @throws DuplicateChildException   if the new parent already has a child of the same name
     * @throws IllegalTreeStateException if this node is already attached to another branch
     * @see NodeCollection#add(ConfigNode, boolean)
     */
    void attachTo(ConfigBranch parent) throws DuplicateChildException, IllegalTreeStateException;

    /**
     * Detaches this node from its parent branch, if any.
     *
     * <p> After this method has returned, this node will be removed from
     * the current parent's {@linkplain ConfigBranch#getItems() children}, and this
     * node's parent will be set to {@code null}.
     *
     * <p> This method has no effect is this node has no parent.
     *
     * @see NodeCollection#remove(Object)
     */
    void detach();

}
