package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigNode;
import me.zeroeightsix.fiber.exception.DuplicateChildException;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A specialized {@link Collection} implementation for use with nodes.
 *
 * <p>Note: This is not a general-purpose collection.
 * Mutating methods in this class <strong>will</strong> mutate external state.
 *
 * <p> Elements in a node collection are considered children of the same tree.
 * For this reason, each element of a node collection must have a distinct name.
 * Mutating methods in this class will also update the {@linkplain ConfigNode#getParent() parent}
 * field of added and removed elements.
 *
 * <p> The iterator returned by the {@code iterator} method traverses the
 * elements in ascending name order (ie. lexicographic order of the nodes' names).
 *
 * <p> Null elements are not permitted. Attempts to insert a null element
 * will throw a {@link NullPointerException}. Attempts to test for the
 * presence of a null element or to remove one will, however, function
 * properly.
 */
public interface NodeCollection extends Collection<ConfigNode> {
    /**
     * Attempts to introduce a new child to this collection.
     *
     * <p> This method behaves as if {@code add(node, false)}.
     *
     * @param child The child to add
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws DuplicateChildException if there was already a child by the same name
     * @throws IllegalStateException if the child cannot be added to this tree at this time
     * @throws NullPointerException if {@code node} is null
     */
    @Override
    boolean add(ConfigNode child) throws DuplicateChildException;

    /**
     * Attempts to introduce a new child to this collection.
     *
     * <p> If this method returns normally, the {@code child} will be attached
     * to this collection's owner.
     *
     * @param child      The child to add
     * @param overwrite whether existing items with the same name should be overwritten
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws DuplicateChildException if there exists a child by the same name that was not overwritten
     * @throws IllegalStateException if the child cannot be added to this tree at this time
     * @throws NullPointerException if {@code node} is null
     * @see ConfigNode#attachTo(ConfigBranch)
     */
    boolean add(ConfigNode child, boolean overwrite) throws DuplicateChildException;

    /**
     * Removes a child from this collection.
     *
     * <p> When this method returns, the {@code child} will be detached from
     * this collection's owner. If the given object is not contained within this
     * collection, this method returns {@code false} with no side effects.
     *
     * @param child the child to remove
     * @return {@code true} if a node was detached as a result of this call
     * @see ConfigNode#detach()
     */
    @Override
    boolean remove(Object child);

    /**
     * Tries to find a child in this collection by name. If a child is found, it will be returned.
     *
     * @param name The name of the child to look for
     * @return the child if found, otherwise {@code null}
     */
    ConfigNode getByName(String name);

    /**
     * Attempts to remove an item from this collection by name.
     *
     * @param name the name of the child that should be removed
     * @return the child if removed, otherwise {@code null}
     */
    @Nullable
    ConfigNode removeByName(String name);
}
