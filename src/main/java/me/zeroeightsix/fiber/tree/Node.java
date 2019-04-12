package me.zeroeightsix.fiber.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface Node extends TreeItem {

    @Nonnull
    Set<TreeItem> getItems();

    @Nullable
    default TreeItem lookup(String name) {
        return getItems()
                .stream()
                .filter(treeItem -> treeItem.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

}
