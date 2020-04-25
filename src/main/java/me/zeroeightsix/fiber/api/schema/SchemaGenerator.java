package me.zeroeightsix.fiber.api.schema;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.impl.MarshallerImpl;
import me.zeroeightsix.fiber.api.serialization.JsonTypeSerializer;
import me.zeroeightsix.fiber.api.serialization.Marshaller;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;
import me.zeroeightsix.fiber.api.tree.ConfigTree;

import javax.annotation.Nullable;
import java.util.Optional;

public class SchemaGenerator {

	@Nullable
	private final Marshaller<JsonElement> marshaller;
	private final JsonTypeSerializer typeSerializer;

	public SchemaGenerator(@Nullable Marshaller<JsonElement> marshaller) {
		this.marshaller = marshaller;
		this.typeSerializer = new JsonTypeSerializer();
	}

	public SchemaGenerator() {
		this(null);
	}

	public JsonObject createSchema(ConfigTree tree) {
		JsonObject object = new JsonObject();

		for (ConfigNode item : tree.getItems()) {// TODO: Maybe allow for custom schema deserializers? / generic metadata
			if (item instanceof ConfigBranch) {
				object.put(item.getName(), this.createSchema((ConfigTree) item));
			} else if (item instanceof ConfigLeaf) {
				object.put(item.getName(), this.createSchema((ConfigLeaf<?>) item));
			}
			// TODO attributes
		}

		return object;
	}

	private JsonObject createSchema(ConfigLeaf<?> item) {
		JsonObject object = new JsonObject();
		JsonObject type = new JsonObject();
		this.typeSerializer.serializeType(item.getConfigType(), type);
		object.put("type", type);
		if (item.getComment() != null) {
			object.put("comment", new JsonPrimitive(item.getComment()));
		}
		if (item.getDefaultValue() != null) {
			Object o = item.getDefaultValue();
			object.put("defaultValue", Optional.ofNullable(this.marshaller != null ? this.marshaller.marshall(o) : null).orElse(MarshallerImpl.getFallback().serialize(o)));
		}
		return object;
	}
}
