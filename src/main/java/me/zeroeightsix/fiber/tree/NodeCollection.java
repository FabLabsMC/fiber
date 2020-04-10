package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;
import java.util.Collection;

public interface NodeCollection extends Collection<ConfigNode> {
    ConfigNodeImpl getByName(String name);

    @Nullable
    ConfigNodeImpl removeByName(String name);
}
