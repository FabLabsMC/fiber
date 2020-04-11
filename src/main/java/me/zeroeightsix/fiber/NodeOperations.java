package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.*;

public class NodeOperations {

    /**
     * Merges two {@code ConfigNode} objects.
     *
     * <p> The first parameter {@code from} remains unchanged, but {@code to} will be mutated and receive all of {@code from}'s children.
     *
     * <p> If both nodes have one or more children with the same name, the child from {@code from} takes priority.
     *
     * @param from  The {@code ConfigNode} that will be read from, but not mutated.
     * @param to    The mutated {@link ConfigBranch} that will inherit <code>from</code>'s values and nodes.
     */
    public static void mergeTo(ConfigTree from, ConfigTreeBuilder to) {
        try {
            for (ConfigNode item : from.getItems()) {
                to.withChild(item, true);
            }
        } catch (FiberException e) {
            throw new RuntimeFiberException("Failed to merge nodes", e);
        }
    }

    /**
     * Merges a leaf node ({@code ConfigLeaf}) into a {@code ConfigNode}.
     *
     * @param value The leaf node to be inherited
     * @param to    The mutated {@link ConfigBranch} that will inherit <code>value</code>
     */
    public static void mergeTo(ConfigLeaf<?> value, ConfigTreeBuilder to) {
        try {
            to.withChild(value, true);
        } catch (FiberException e) {
            throw new RuntimeFiberException("Failed to merge value", e);
        }
    }

    public static <T> void mergeTo(Property<T> from, Property<T> to) {
        to.setValue(from.getValue());
    }
}
