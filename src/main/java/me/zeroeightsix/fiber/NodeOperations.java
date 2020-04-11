package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.exception.DuplicateChildException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.ConfigBranch;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.ConfigTree;
import me.zeroeightsix.fiber.tree.Property;

import java.util.Iterator;

public class NodeOperations {

    /**
     * Merges two {@code ConfigTree}s.
     *
     * <p> The first parameter {@code from} will be stripped of its children,
     * and {@code to} will receive all of {@code from}'s children.
     *
     * <p> If both nodes have one or more children with the same name, the child from {@code from} takes priority.
     *
     * @param from  The {@code ConfigNode} that will be read from, but not mutated.
     * @param to    The mutated {@link ConfigBranch} that will inherit <code>from</code>'s values and nodes.
     */
    public static void moveChildren(ConfigTree from, ConfigTree to) {
        try {
            for (Iterator<ConfigNode> it = from.getItems().iterator(); it.hasNext(); ) {
                ConfigNode item = it.next();
                it.remove();
                to.getItems().add(item, true);
            }
        } catch (DuplicateChildException e) {
            throw new RuntimeFiberException("Failed to merge nodes", e);
        }
    }

    /**
     * Moves a node ({@code ConfigNode}) to a new parent {@code ConfigTree}.
     *
     * <p> If the moved node has an existing parent, it will be detached.
     * If the new parent has an existing node with the same name, it will be overwritten.
     *
     * @param value The leaf node to be inherited
     * @param to    The mutated {@link ConfigBranch} that will inherit <code>value</code>
     */
    public static void moveNode(ConfigNode value, ConfigTree to) {
        try {
            ConfigBranch parent = value.getParent();
            if (parent != null) {
                parent.getItems().remove(value);
            }
            to.getItems().add(value, true);
        } catch (DuplicateChildException e) {
            throw new RuntimeFiberException("Failed to merge value", e);
        }
    }

    public static <T> void copyValue(Property<T> from, Property<T> to) {
        to.setValue(from.getValue());
    }
}
