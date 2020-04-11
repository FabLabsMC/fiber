package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.DuplicateChildException;

import javax.annotation.Nullable;
import java.util.Collection;

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
     * @param child      The child to add
     * @param overwrite whether existing items with the same name should be overwritten
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws DuplicateChildException if there exists a child by the same name that was not overwritten
     * @throws IllegalStateException if the child cannot be added to this tree at this time
     * @throws NullPointerException if {@code node} is null
     */
    boolean add(ConfigNode child, boolean overwrite) throws DuplicateChildException;

    ConfigNode getByName(String name);

    /**
     * Attempts to remove an item from this node by name.
     *
     * @param name the name of the child that should be removed
     * @return the child if removed, otherwise {@code null}
     */
    @Nullable
    ConfigNode removeByName(String name);
}
