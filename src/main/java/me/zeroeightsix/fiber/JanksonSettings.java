package me.zeroeightsix.fiber;

import blue.endless.jankson.*;
import blue.endless.jankson.api.SyntaxError;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class JanksonSettings {

	@Nullable
	private Marshaller<JsonElement> marshaller;

	public JanksonSettings(@Nullable Marshaller<JsonElement> marshaller) {
		this.marshaller = marshaller;
	}

	public JanksonSettings() {
		this(null);
	}

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

	private void deserialize(Node node, JsonObject element) throws FiberException {
		JsonObject object = element;
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

			TreeItem item = node.lookup(key);
			if (item != null) {
				if (item instanceof Property) {
					setPopertyValue((Property<?>) item, child);
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

	private <T> void setPopertyValue(Property<T> property, JsonElement child) {
		Class<T> type = property.getType();
		property.setValue(marshall(type, child));
	}

	public void serialize(Node node, OutputStream stream, boolean compress) throws IOException {
		JsonObject object = serialize(node);
		stream.write(object.toJson(!compress, !compress).getBytes());
	}

	private JsonObject serialize(Node node) {
		JsonObject object = new JsonObject();

		node.getItems().forEach(treeItem -> {
			String name = null;

			if (treeItem instanceof Node) {
				Node subNode = (Node) treeItem;
				object.put((name = subNode.getName()), serialize(subNode));
			} else if (treeItem instanceof HasValue) {
				object.put((name = treeItem.getName()), serialize((HasValue<?>) treeItem));
			}

			if (name != null && treeItem instanceof Commentable) {
				object.setComment(name, ((Commentable) treeItem).getComment());
			}
		});

		return object;
	}

	private JsonElement serialize(HasValue<?> hasValue) {
		JsonElement element = marshaller != null ? marshaller.marshall(hasValue.getValue()) : null;
		if (element != null) return element;
		return blue.endless.jankson.impl.MarshallerImpl.getFallback().serialize(hasValue.getValue());
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
			return JanksonSettings.this.marshall(type, this.value);
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

	private <A> A marshall(Class<A> type, JsonElement value) {
		A object = marshaller != null ? marshaller.marshallReverse(type, value) : null;
		if (object != null) return object;
		return blue.endless.jankson.impl.MarshallerImpl.getFallback().marshall(type, value);
	}

}
