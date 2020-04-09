package me.zeroeightsix.fiber.exception;

import me.zeroeightsix.fiber.tree.ConfigQuery;
import me.zeroeightsix.fiber.tree.Node;
import me.zeroeightsix.fiber.tree.NodeLike;
import me.zeroeightsix.fiber.tree.TreeItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Signals that an exception occurred while running a {@link ConfigQuery}.
 *
 * <p> This class is the general class of exceptions produced by failed
 * config tree queries.
 */
public class FiberQueryException extends FiberException {
    private final NodeLike invalidTree;

    public FiberQueryException(String message, NodeLike invalidTree) {
        super(message + (invalidTree instanceof Node && ((Node) invalidTree).getName() != null
                ? " (in subtree " + ((Node) invalidTree).getName() + ")" : ""));
        this.invalidTree = invalidTree;
    }

    public FiberQueryException(String message, Throwable cause, NodeLike invalidTree) {
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
    public NodeLike getErrorParent() {
        return invalidTree;
    }

    /**
     * Checked exception thrown when a query fails to find a child
     * with a given name from an ancestor node.
     */
    public static class MissingChild extends FiberQueryException {
        private final String missingNodeName;

        public MissingChild(String name, NodeLike invalidTree) {
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
            return missingNodeName;
        }
    }

    /**
     * Checked exception thrown when a query finds a node of
     * a different type than expected.
     */
    public static class WrongType extends FiberQueryException {
        private final TreeItem invalidItem;
        private final Class<?> expectedNodeType;
        @Nullable
        private final Class<?> expectedValueType;

        public WrongType(NodeLike invalidTree, TreeItem invalidItem, Class<?> expectedNodeType, @Nullable Class<?> expectedValueType) {
            super("Expected node of type " + expectedNodeType.getSimpleName()
                    + (expectedValueType == null ? "" : "<" + expectedValueType.getSimpleName() + ">")
                    + ", got " + invalidItem, invalidTree);
            this.invalidItem = invalidItem;
            this.expectedNodeType = expectedNodeType;
            this.expectedValueType = expectedValueType;
        }

        public TreeItem getInvalidNode() {
            return invalidItem;
        }

        public Class<?> getExpectedNodeType() {
            return expectedNodeType;
        }

        /**
         * Returns the type of property values expected by the query.
         *
         * <p> If the query expected an ancestor node to be found, this method returns {@code null}.
         *
         * @return the expected value type, or {@code null} if the query did not expect a property
         */
        @Nullable
        public Class<?> getExpectedValueType() {
            return expectedValueType;
        }
    }
}
