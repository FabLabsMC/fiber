package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.FiberQueryException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ConfigQueryTest {

    @Test
    void run() throws FiberQueryException {
        AtomicReference<ConfigLeaf<Integer>> a = new AtomicReference<>();
        ConfigTree tree = ConfigTree.builder()
                .fork("child")
                .fork("stuff")
                .beginValue("A", 10)
                .finishValue(a::set)
                .finishBranch()
                .finishBranch()
                .build();
        ConfigQuery<?> query1 = ConfigQuery.leaf(Integer.class , "child", "stuff", "A");
        assertEquals(a.get(), query1.run(tree));
        assertTrue(query1.search(tree).isPresent());

        ConfigQuery<?> query2 = ConfigQuery.leaf(Integer.class, "child", "more");
        assertFalse(query2.search(tree).isPresent());
        assertThrows(FiberQueryException.MissingChild.class, () -> query2.run(tree));

        ConfigQuery<?> query3 = ConfigQuery.branch("child", "stuff", "A");
        assertThrows(FiberQueryException.WrongType.class, () -> query3.run(tree));
        assertFalse(query3.search(tree).isPresent());

        ConfigQuery<?> query4 = ConfigQuery.leaf(String.class, "child", "stuff", "A");
        assertThrows(FiberQueryException.WrongType.class, () -> query4.run(tree));
        assertFalse(query4.search(tree).isPresent());

        ConfigQuery<?> query5 = ConfigQuery.leaf(Integer.class, "child", "stuff", "A", "more");
        assertThrows(FiberQueryException.WrongType.class, () -> query5.run(tree));
        assertFalse(query5.search(tree).isPresent());
    }
}