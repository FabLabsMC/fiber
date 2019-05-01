package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.FiberException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
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

    default TreeItem add(@Nonnull TreeItem item) throws FiberException {
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
                    throw new FiberException("Attempt to replace transparent node by non-property node " + item.getName());
                }
            } else {
                throw new FiberException("Attempt to replace non-transparent node " + existing.getName());
            }
        }
        return item;
    }

    default TreeItem remove(String name) {
        Optional<TreeItem> itemOptional = getItems().stream().filter(item -> item.getName().equals(name)).findAny();
        if (!itemOptional.isPresent()) return null;
        TreeItem item = itemOptional.get();
        getItems().remove(item);
        return item;
    }

    default Node fork(String name) throws FiberException {
        return (Node) add(new ConfigNode(name, null));
    }

}
