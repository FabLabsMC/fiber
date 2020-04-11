package me.zeroeightsix.fiber.serialization;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import me.zeroeightsix.fiber.Identifier;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JanksonSerializer implements Serializer<JsonObject> {

	private static final Identifier IDENTIFIER = new Identifier("fiber", "jankson");

	private final boolean compress;
	@Nonnull
	private final Marshaller<JsonElement> marshaller;

	public JanksonSerializer() {
		this(JanksonFallbackMarshaller.INSTANCE, false);
	}

	public JanksonSerializer(@Nonnull Marshaller<JsonElement> marshaller, boolean compress) {
		this.compress = compress;
		this.marshaller = marshaller;
	}

	@Override
	public JsonObject deserialize(ConfigTree tree, InputStream stream) throws IOException, FiberException {
		Jankson jankson = Jankson.builder().build();
		JsonObject object;
		try {
			object = jankson.load(stream);
		} catch (SyntaxError syntaxError) {
			throw new FiberException("Configuration file was malformed", syntaxError);
		}
		return deserialize(tree, object);
	}

	@Override
	public JsonObject deserialize(ConfigTree tree, JsonObject element) throws FiberException {
		JsonObject leftovers = new JsonObject();
		for (Map.Entry<String, JsonElement> entry : element.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

			ConfigNode item = tree.lookup(key);
			if (item != null) {
				if (item instanceof Property) {
					setPropertyValue((Property<?>) item, child);
				} else if (item instanceof ConfigBranch && child instanceof JsonObject) {
					JsonObject childLeftovers = deserialize((ConfigTree) item, (JsonObject) child);
					if (!childLeftovers.isEmpty()) {
						leftovers.put(key, childLeftovers);
					}
				} else {
					throw new FiberException("Value read for non-property node: " + item.getName());
				}
			} else {
				leftovers.put(key, child);
			}
		}
		return leftovers;
	}

	private JsonElement serialize(HasValue<?> hasValue) {
		return marshaller.marshall(hasValue.getValue());
	}

	@Override
	public void serialize(ConfigTree tree, @Nullable JsonObject additionalData, OutputStream out) throws IOException {
		JsonObject object = serialize(tree);
		if (additionalData != null) {
			object.putAll(additionalData);
		}
		out.write(object.toJson(!compress, !compress).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public JsonObject serialize(ConfigTree tree) {
		JsonObject object = new JsonObject();

		for (ConfigNode treeItem : tree.getItems()) {
			String name = null;

			if (treeItem instanceof ConfigBranch) {
				ConfigBranch subNode = (ConfigBranch) treeItem;
				if (!subNode.isSerializedSeparately()) {
					object.put((name = subNode.getName()), serialize(subNode));
				}
			} else if (treeItem instanceof HasValue) {
				object.put((name = treeItem.getName()), serialize((HasValue<?>) treeItem));
			}

			if (name != null && treeItem instanceof Commentable) {
				object.setComment(name, ((Commentable) treeItem).getComment());
			}
		}

		return object;
	}

	private <A> A marshall(Class<A> type, JsonElement value) {
		return marshaller.marshallReverse(type, value);
	}

	private <T> void setPropertyValue(Property<T> property, JsonElement child) {
		Class<T> type = property.getType();
		property.setValue(marshall(type, child));
	}

	@Override
	public Identifier getIdentifier() {
		return IDENTIFIER;
	}

	private static class JanksonFallbackMarshaller implements Marshaller<JsonElement> {
		private static final JanksonFallbackMarshaller INSTANCE = new JanksonFallbackMarshaller();

		private final blue.endless.jankson.api.Marshaller marshaller = Jankson.builder().build().getMarshaller();

		private JanksonFallbackMarshaller() {}

		@Override
		public JsonElement marshall(Object value) {
			return marshaller.serialize(value);
		}

		@Override
		public <A> A marshallReverse(Class<A> type, JsonElement value) {
			return marshaller.marshall(type, value);
		}
	}

}
