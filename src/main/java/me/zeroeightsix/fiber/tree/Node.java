package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exceptions.FiberException;

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

    default void add(@Nonnull TreeItem item) throws FiberException {
        TreeItem existing = lookup(item.getName());
        if (existing == null) {
            getItems().add(item);
        } else {
            if (existing instanceof Transparent) {
                if (item instanceof Property) {
                    Class type = ((Property) item).getType();
                    ((Property) item).setValue(((Transparent) existing).marshal(type));
                    getItems().remove(existing);
                    getItems().add(item);
                } else {
                    throw new FiberException("Transparent node replaced by non-property node");
                }
            } else {
                throw new FiberException("Attempt to replace non-transparent node");
            }
        }
    }

}
