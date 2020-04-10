package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.api.exception.FiberQueryException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ConfigQueryTest {

    @Test
    void run() throws FiberQueryException {
        AtomicReference<ConfigLeaf<Integer>> a = new AtomicReference<>();
        ConfigGroupImpl tree = new ConfigTreeBuilder()
                .fork("child")
                .fork("stuff")
                .beginValue("A", 10)
                .finishValue(a::set)
                .finishNode()
                .finishNode()
                .build();
        ConfigQuery<?> query1 = ConfigQuery.property(Integer.class , "child", "stuff", "A");
        assertEquals(a.get(), query1.run(tree));
        assertTrue(query1.search(tree).isPresent());

        ConfigQuery<?> query2 = ConfigQuery.property(Integer.class, "child", "more");
        assertFalse(query2.search(tree).isPresent());
        assertThrows(FiberQueryException.MissingChild.class, () -> query2.run(tree));

        ConfigQuery<?> query3 = ConfigQuery.subtree("child", "stuff", "A");
        assertThrows(FiberQueryException.WrongType.class, () -> query3.run(tree));
        assertFalse(query3.search(tree).isPresent());

        ConfigQuery<?> query4 = ConfigQuery.property(String.class, "child", "stuff", "A");
        assertThrows(FiberQueryException.WrongType.class, () -> query4.run(tree));
        assertFalse(query4.search(tree).isPresent());

        ConfigQuery<?> query5 = ConfigQuery.property(Integer.class, "child", "stuff", "A", "more");
        assertThrows(FiberQueryException.WrongType.class, () -> query5.run(tree));
        assertFalse(query5.search(tree).isPresent());
    }
}