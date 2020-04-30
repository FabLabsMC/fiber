package io.github.fablabsmc.fablabs.impl.fiber.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberQueryException;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigQuery;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import org.junit.jupiter.api.Test;

class ConfigQueryTest {
	@Test
	void run() throws FiberQueryException {
		AtomicReference<ConfigLeaf<?>> a = new AtomicReference<>();
		ConfigTree tree = ConfigTree.builder()
				.fork("child")
				.fork("stuff")
				.beginValue("A", ConfigTypes.INTEGER, 10)
				.finishValue(a::set)
				.finishBranch()
				.finishBranch()
				.build();
		ConfigQuery<?> query1 = ConfigQuery.leaf(ConfigTypes.INTEGER.getSerializedType(), "child", "stuff", "A");
		assertEquals(a.get(), query1.run(tree));
		assertTrue(query1.search(tree).isPresent());

		ConfigQuery<?> query2 = ConfigQuery.leaf(ConfigTypes.INTEGER.getSerializedType(), "child", "more");
		assertFalse(query2.search(tree).isPresent());
		assertThrows(FiberQueryException.MissingChild.class, () -> query2.run(tree));

		ConfigQuery<?> query3 = ConfigQuery.branch("child", "stuff", "A");
		assertThrows(FiberQueryException.WrongType.class, () -> query3.run(tree));
		assertFalse(query3.search(tree).isPresent());

		ConfigQuery<?> query4 = ConfigQuery.leaf(ConfigTypes.STRING.getSerializedType(), "child", "stuff", "A");
		assertThrows(FiberQueryException.WrongType.class, () -> query4.run(tree));
		assertFalse(query4.search(tree).isPresent());

		ConfigQuery<?> query5 = ConfigQuery.leaf(ConfigTypes.INTEGER.getSerializedType(), "child", "stuff", "A", "more");
		assertThrows(FiberQueryException.WrongType.class, () -> query5.run(tree));
		assertFalse(query5.search(tree).isPresent());
	}
}
