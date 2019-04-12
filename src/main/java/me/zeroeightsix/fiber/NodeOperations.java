package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.tree.*;

public class NodeOperations {

    /**
     * Merges two {@link ConfigNode} objects
     * @param from  The ConfigNode that will be read from, but not mutated.
     * @param to    The mutated {@link ConfigNode} that will inherit <code>from</code>'s values and nodes.
     */
    public static void mergeTo(Node from, Node to) {
        // TODO
    }

    /**
     * Merges a leaf node ({@link ConfigValue}) into a {@link ConfigNode}
     * @param value The leaf node to be inherited
     * @param to    The mutated {@link ConfigNode} that will inherit <code>value</code>
     */
    public static void mergeTo(ConfigValue value, Node to) {
        // TODO
        // to.getItems().add()
    }

    public static <T> void mergeTo(ConfigValue<T> from, ConfigValue<T> to) {
        from.setValue(to.getValue());
    }
}
