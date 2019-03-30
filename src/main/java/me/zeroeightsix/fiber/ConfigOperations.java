package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.ir.ConfigNode;
import me.zeroeightsix.fiber.ir.ConfigValue;

public class ConfigOperations {

    /**
     * Merges two {@link ConfigNode} objects
     * @param from  The ConfigNode that will be read from, but not mutated.
     * @param to    The mutated {@link ConfigNode} that will inherit <code>from</code>'s values and nodes.
     */
    public static void mergeTo(ConfigNode from, ConfigNode to) {
        from.getSettingsImmutable().forEach((name, value) -> mergeTo(value, to));
        from.getSubSettingsImmutable().forEach((name, node) -> mergeTo(node, to.sub(name)));
    }

    /**
     * Merges a leaf node ({@link ConfigValue}) into a {@link ConfigNode}
     * @param value The leaf node to be inherited
     * @param to    The mutated {@link ConfigNode} that will inherit <code>value</code>
     */
    public static void mergeTo(ConfigValue value, ConfigNode to) {
        to.putValue(value);
    }

    public static <T> void mergeTo(ConfigValue<T> from, ConfigValue<T> to) {
        from.setValue(to.getValue());
    }
}
