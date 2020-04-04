package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
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
     * @param to    The mutated {@link ConfigNode} that will inherit <code>from</code>'s values and nodes.
     */
    public static void mergeTo(ConfigTree from, ConfigNodeBuilder to) {
        try {
            for (TreeItem item : from.getItems()) {
                to.add(item, true);
            }
        } catch (FiberException e) {
            throw new RuntimeFiberException("Failed to merge nodes", e);
        }
    }

    /**
     * Merges a leaf node ({@code ConfigValue}) into a {@code ConfigNode}.
     *
     * @param value The leaf node to be inherited
     * @param to    The mutated {@link ConfigNode} that will inherit <code>value</code>
     */
    public static void mergeTo(ConfigValue<?> value, ConfigNodeBuilder to) {
        try {
            to.add(value, true);
        } catch (FiberException e) {
            throw new RuntimeFiberException("Failed to merge value", e);
        }
    }

    public static <T> void mergeTo(Property<T> from, Property<T> to) {
        to.setValue(from.getValue());
    }
}
