package me.zeroeightsix.fiber.api.serialization;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import me.zeroeightsix.fiber.api.FiberId;
import me.zeroeightsix.fiber.api.exception.FiberException;
import me.zeroeightsix.fiber.api.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.api.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class JanksonSerializer implements Serializer<JsonObject> {

	private static final FiberId IDENTIFIER = new FiberId("fiber", "jankson");

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
		return this.deserialize(tree, object);
	}

	@Override
	public JsonObject deserialize(ConfigTree tree, JsonObject element) throws FiberException {
		JsonObject leftovers = new JsonObject();
		for (Map.Entry<String, JsonElement> entry : element.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

			ConfigNode item = tree.lookup(key);
			if (item != null) {
				if (item instanceof ConfigLeaf) {
					this.setPropertyValue((ConfigLeaf<?>) item, child);
				} else if (item instanceof ConfigBranch && child instanceof JsonObject) {
					JsonObject childLeftovers = this.deserialize((ConfigTree) item, (JsonObject) child);
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

	JsonElement serialize(HasValue<?> configValue) {
		return marshaller.marshall(configValue.getValue());
	}

	@Override
	public void serialize(ConfigTree tree, @Nullable JsonObject additionalData, OutputStream out) throws IOException {
		JsonObject object = this.serialize(tree);
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
					name = Objects.requireNonNull(subNode.getName());
					object.put(name, this.serialize(subNode));
				}
			} else if (treeItem instanceof HasValue) {
				object.put((name = treeItem.getName()), this.serialize((HasValue<?>) treeItem));
			}

			if (name != null && treeItem instanceof Commentable) {
				object.setComment(name, ((Commentable) treeItem).getComment());
			}
		}

		return object;
	}

	@Nullable
	<A> A marshall(Class<A> type, JsonElement value) {
		return marshaller.marshallReverse(type, value);
	}

	private <T> void setPropertyValue(ConfigLeaf<T> property, JsonElement child) {
		Class<T> type = property.getType();
		// TODO figure out how we want to handle null values
		T deserialized = this.marshall(type, child);
		try {
			property.setValue(deserialized);
		} catch (NullPointerException e) {
			if (deserialized == null) {	// probably caused by the unexpected null input
				throw new RuntimeFiberException("Failed to deserialize input '" + child + "' for " + property, e);
			}
			throw e;
		}
	}

	@Override
	public FiberId getIdentifier() {
		return IDENTIFIER;
	}

	public static Marshaller<JsonElement> extendDefaultMarshaller(Marshaller<JsonElement> marshaller) {
		return new Marshaller<JsonElement>() {
			@Override
			public JsonElement marshall(Object value) {
				JsonElement object = marshaller.marshall(value);
				if (object == null) return JanksonFallbackMarshaller.INSTANCE.marshall(value);
				return object;
			}

			@Override
			public <A> A marshallReverse(Class<A> type, JsonElement value) {
				A object = marshaller.marshallReverse(type, value);
				if (object == null) return JanksonFallbackMarshaller.INSTANCE.marshallReverse(type, value);
				return object;
			}
		};
	}

	private static class JanksonFallbackMarshaller implements Marshaller<JsonElement> {
		private static final JanksonFallbackMarshaller INSTANCE = new JanksonFallbackMarshaller();

		private final blue.endless.jankson.api.Marshaller marshaller = Jankson.builder().build().getMarshaller();

		private JanksonFallbackMarshaller() {}

		@Override
		public JsonElement marshall(Object value) {
			if (value instanceof BigDecimal) {
				return new JsonPrimitive(value);
			}
			return this.marshaller.serialize(value);
		}

		@Override
		public <A> A marshallReverse(Class<A> type, JsonElement value) {
			if (type == BigDecimal.class) {
				if (value instanceof JsonPrimitive) {
					return type.cast(new BigDecimal(((JsonPrimitive) value).asString()));
				}
				return null;
			}
			return marshaller.marshall(type, value);
		}
	}

}
