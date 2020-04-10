package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;
import java.util.Collection;

public interface NodeCollection extends Collection<ConfigNode> {
    ConfigNode getByName(String name);

    @Nullable
    ConfigNode removeByName(String name);
}
