package me.zeroeightsix.fiber.exception;

import me.zeroeightsix.fiber.schema.ConfigType;
import me.zeroeightsix.fiber.tree.ConfigBranch;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.ConfigQuery;
import me.zeroeightsix.fiber.tree.ConfigTree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Signals that an exception occurred while running a {@link ConfigQuery}.
 *
 * <p> This class is the general class of exceptions produced by failed
 * config tree queries.
 */
public class FiberQueryException extends FiberException {
    private final ConfigTree invalidTree;

    public FiberQueryException(String message, ConfigTree invalidTree) {
        super(message + (invalidTree instanceof ConfigBranch && ((ConfigBranch) invalidTree).getName() != null
                ? " (in branch " + ((ConfigBranch) invalidTree).getName() + ")" : ""));
        this.invalidTree = invalidTree;
    }

    public FiberQueryException(String message, Throwable cause, ConfigTree invalidTree) {
        super(message, cause);
        this.invalidTree = invalidTree;
    }

    /**
     * Returns the last valid ancestor before which the error occurred.
     *
     * <p> The invalid tree may be the tree directly passed to the query,
     * or it may be a descendant node.
     *
     * @return the parent of the erroring node.
     */
    public ConfigTree getErrorParent() {
        return this.invalidTree;
    }

    /**
     * Checked exception thrown when a query fails to find a child
     * with a given name from an ancestor node.
     */
    public static class MissingChild extends FiberQueryException {
        private final String missingNodeName;

        public MissingChild(String name, ConfigTree invalidTree) {
            super("Missing child " + name, invalidTree);
            this.missingNodeName = name;
        }

        /**
         * Returns the name of the missing child.
         *
         * @return the name of the missing child
         */
        @Nonnull
        public String getMissingChildName() {
            return this.missingNodeName;
        }
    }

    /**
     * Checked exception thrown when a query finds a node of
     * a different type than expected.
     */
    public static class WrongType extends FiberQueryException {
        private final ConfigNode invalidItem;
        private final Class<?> expectedNodeType;
        @Nullable
        private final ConfigType<?, ?> expectedValueType;

        public WrongType(ConfigTree invalidTree, ConfigNode invalidItem, Class<?> expectedNodeType, @Nullable ConfigType<?, ?> expectedValueType) {
            super("Expected node of type " + expectedNodeType.getSimpleName()
                    + (expectedValueType == null ? "" : "<" + expectedValueType + ">")
                    + ", got " + invalidItem, invalidTree);
            this.invalidItem = invalidItem;
            this.expectedNodeType = expectedNodeType;
            this.expectedValueType = expectedValueType;
        }

        public ConfigNode getInvalidNode() {
            return this.invalidItem;
        }

        public Class<?> getExpectedNodeType() {
            return this.expectedNodeType;
        }

        /**
         * Returns the type of property values expected by the query.
         *
         * <p> If the query expected an ancestor node to be found, this method returns {@code null}.
         *
         * @return the expected value type, or {@code null} if the query did not expect a property
         */
        @Nullable
        public ConfigType<?, ?> getExpectedValueType() {
            return this.expectedValueType;
        }
    }
}
