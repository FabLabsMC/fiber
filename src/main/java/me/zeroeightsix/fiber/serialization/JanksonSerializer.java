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
	public void deserialize(Node node, InputStream stream) throws IOException, FiberException {
		Jankson jankson = Jankson.builder().build();
		JsonObject object;
		try {
			object = jankson.load(stream);
		} catch (SyntaxError syntaxError) {
			throw new FiberException("Configuration file was malformed", syntaxError);
		}
		deserialize(node, object);
	}

	@Override
	public void deserialize(Node node, JsonObject element) throws FiberException {
		for (Map.Entry<String, JsonElement> entry : element.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

			TreeItem item = node.lookup(key);
			if (item != null) {
				if (item instanceof Property) {
					setPropertyValue((Property<?>) item, child);
				} else if (item instanceof Node && child instanceof JsonObject) {
					deserialize((Node) item, (JsonObject) child);
				} else {
					throw new FiberException("Value read for non-property node: " + item.getName());
				}
			} else {
				JanksonTransparentNode transparentNode = new JanksonTransparentNode(key, child);
				node.add(transparentNode);
			}
		}
	}

	private JsonElement serialize(HasValue<?> hasValue) {
		return marshaller.marshall(hasValue.getValue());
	}

	@Override
	public void serialize(Node node, OutputStream stream) throws IOException {
		JsonObject object = serialize(node);
		stream.write(object.toJson(!compress, !compress).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public JsonObject serialize(Node node) {
		JsonObject object = new JsonObject();

		for (TreeItem treeItem : node.getItems()) {
			String name = null;

			if (treeItem instanceof Node) {
				Node subNode = (Node) treeItem;
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

	private class JanksonTransparentNode implements Transparent {
		private final String name;
		private final JsonElement value;

		public JanksonTransparentNode(String name, JsonElement value) {
			this.name = name;
			this.value = value;
		}

		@Nullable
		@Override
		public <A> A marshall(Class<A> type) {
			return JanksonSerializer.this.marshall(type, this.value);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[name=" + getName() + "]";
		}
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
