package me.zeroeightsix.fiber.api.schema;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.impl.MarshallerImpl;
import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.api.serialization.Marshaller;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;
import me.zeroeightsix.fiber.api.tree.ConfigTree;
import me.zeroeightsix.fiber.impl.constraint.ValuedConstraint;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class SchemaGenerator {

	@Nullable
	private final Marshaller<JsonElement> marshaller;

	public SchemaGenerator(@Nullable Marshaller<JsonElement> marshaller) {
		this.marshaller = marshaller;
	}

	public SchemaGenerator() {
		this(null);
	}

	public JsonObject createSchema(ConfigTree tree) {
		JsonObject object = new JsonObject();

		for (ConfigNode item : tree.getItems()) {// TODO: Maybe allow for custom schema deserialisers? / generic metadata
			if (item instanceof ConfigBranch) {
				object.put(item.getName(), this.createSchema((ConfigTree) item));
			} else if (item instanceof ConfigLeaf) {
				object.put(item.getName(), this.createSchema((ConfigLeaf<?, ?>) item));
			}
			// TODO attributes
		}

		return object;
	}

	private JsonObject createSchema(ConfigLeaf<?, ?> item) {
		JsonObject object = new JsonObject();
		JsonObject type = new JsonObject();
		type.put("kind", new JsonPrimitive(item.getConfigType().getKind().getIdentifier()));
		type.put("constraints", this.createSchema(item.getConfigType().getConstraints()));
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

	private JsonElement createSchema(Collection<? extends Constraint<?>> constraintList) {
		JsonArray array = new JsonArray();
		for (Constraint<?> constraint : constraintList) {
			JsonObject object = new JsonObject();
			object.put("identifier", new JsonPrimitive(constraint.getType().getIdentifier().toString()));
			if (constraint instanceof ValuedConstraint) {
				// TODO marshall properly (specifically NumberRange and Pattern)
				object.put("value", new JsonPrimitive(((ValuedConstraint<?, ?>) constraint).getConstraintValue()));
			}
			array.add(object);
		}
		return array;
	}

}
