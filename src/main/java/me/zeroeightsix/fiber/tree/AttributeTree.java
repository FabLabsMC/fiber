package me.zeroeightsix.fiber.tree;

public interface AttributeTree extends ConfigTree {
    /**
     * Returns the node holding this attribute tree.
     *
     * @return the attributes' holder
     */
    ConfigNode getAttributeHolder();
}
