package io.github.fablabsmc.fablabs.impl.fiber.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.ValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.Commentable;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigNode;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

/**
 * Static class that houses Fiber's serialization and deserialization algorithms.
 */
public final class FiberSerialization {
	private FiberSerialization() {
	}

	public static <A, T> void serialize(ConfigTree tree, OutputStream out, ValueSerializer<A, T> ctx) throws IOException {
		T target = ctx.newTarget();

		for (ConfigNode node : tree.getItems()) {
			A elem = serializeNode(node, ctx);

			if (elem != null) {
				ctx.putElement(node.getName(), elem, target);
			}
		}

		ctx.writeTarget(target, out);
	}

	public static <A, T> void deserialize(ConfigTree tree, InputStream in, ValueSerializer<A, T> ctx) throws IOException, ValueDeserializationException {
		T target = ctx.readTarget(in);

		for (ConfigNode node : tree.getItems()) {
			Optional<A> elem = ctx.getElement(node.getName(), target);

			if (elem.isPresent()) {
				deserializeNode(node, elem.get(), ctx);
			}
		}
	}

	public static <A> A serializeNode(ConfigNode node, ValueSerializer<A, ?> ctx) {
		String name = Objects.requireNonNull(node.getName());
		String comment;

		if (node instanceof Commentable) {
			comment = ((Commentable) node).getComment();
		} else {
			comment = null;
		}

		if (node instanceof ConfigBranch) {
			ConfigBranch branch = (ConfigBranch) node;

			if (!branch.isSerializedSeparately()) {
				Map<String, A> map = new HashMap<>();

				for (ConfigNode subNode : branch.getItems()) {
					map.put(subNode.getName(), serializeNode(node, ctx));
				}

				return ctx.serializeMap(map);
			}
		} else if (node instanceof ConfigLeaf<?>) {
			ConfigLeaf<?> leaf = (ConfigLeaf<?>) node;
			return serializeValue(leaf, ctx);
		}

		return null;
	}

	private static <T, A> A serializeValue(ConfigLeaf<T> leaf, ValueSerializer<A, ?> ctx) {
		return leaf.getConfigType().serializeValue(leaf.getValue(), ctx);
	}

	public static <A> void deserializeNode(ConfigNode node, A elem, ValueSerializer<A, ?> ctx) throws ValueDeserializationException {
		if (node instanceof ConfigBranch) {
			ConfigBranch branch = (ConfigBranch) node;
			Map<String, A> map = ctx.deserializeMap(elem);

			for (ConfigNode subNode : branch.getItems()) {
				A subElem = map.get(subNode.getName());

				if (subElem != null) {
					deserializeNode(subNode, subElem, ctx);
				}
			}
		} else if (node instanceof ConfigLeaf<?>) {
			ConfigLeaf<?> leaf = (ConfigLeaf<?>) node;
			deserializeValue(leaf, elem, ctx);
		}
	}

	private static <T, A> void deserializeValue(ConfigLeaf<T> leaf, A elem, ValueSerializer<A, ?> ctx) throws ValueDeserializationException {
		leaf.setValue(leaf.getConfigType().deserializeValue(elem, ctx));
	}
}
