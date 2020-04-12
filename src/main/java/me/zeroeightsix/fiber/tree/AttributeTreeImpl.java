package me.zeroeightsix.fiber.tree;

import java.util.Collections;

public class AttributeTreeImpl extends ConfigBranchImpl implements AttributeTree {
    private final ConfigNode attributeHolder;

    public AttributeTreeImpl(ConfigNode attributeHolder) {
        super(null, null, Collections.emptyList(), false);
        this.attributeHolder = attributeHolder;
    }

    @Override
    public ConfigNode getAttributeHolder() {
        return this.attributeHolder;
    }
}
