package io.github.fablabsmc.fablabs.impl.fiber.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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
			serializeNode(node, target, ctx);
		}

		ctx.writeTarget(target, out);
	}

	public static <A, T> void deserialize(ConfigTree tree, InputStream in, ValueSerializer<A, T> ctx) throws IOException, ValueDeserializationException {
		T target = ctx.readTarget(in);

		for (Iterator<Map.Entry<String, A>> itr = ctx.elements(target); itr.hasNext(); ) {
			Map.Entry<String, A> entry = itr.next();
			ConfigNode node = tree.lookup(entry.getKey());
			A elem = entry.getValue();

			if (node != null) {
				deserializeNode(node, elem, ctx);
			}
		}
	}

	public static <A, T> void serializeNode(ConfigNode node, T target, ValueSerializer<A, T> ctx) {
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
				T subTarget = ctx.newTarget();

				for (ConfigNode subNode : branch.getItems()) {
					serializeNode(subNode, subTarget, ctx);
				}

				ctx.addSubElement(branch.getName(), subTarget, target);
			}
		} else if (node instanceof ConfigLeaf<?>) {
			ConfigLeaf<?> leaf = (ConfigLeaf<?>) node;
			ctx.addElement(leaf.getName(), serializeValue(leaf, ctx), target);
		}
	}

	private static <T, A> A serializeValue(ConfigLeaf<T> leaf, ValueSerializer<A, ?> ctx) {
		return leaf.getConfigType().serializeValue(leaf.getValue(), ctx);
	}

	public static <A, T> void deserializeNode(ConfigNode node, A elem, ValueSerializer<A, T> ctx) throws ValueDeserializationException {
		if (node instanceof ConfigBranch) {
			ConfigBranch branch = (ConfigBranch) node;

			for (Iterator<Map.Entry<String, A>> itr = ctx.subElements(elem); itr.hasNext(); ) {
				Map.Entry<String, A> entry = itr.next();
				ConfigNode subNode = branch.lookup(entry.getKey());
				A subElem = entry.getValue();

				if (subNode != null) {
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
