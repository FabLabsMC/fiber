package me.zeroeightsix.fiber.schema;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.impl.MarshallerImpl;
import me.zeroeightsix.fiber.Identifier;
import me.zeroeightsix.fiber.api.builder.constraint.CompositeConstraintsBuilder;
import me.zeroeightsix.fiber.api.constraint.Constraint;
import me.zeroeightsix.fiber.impl.constraint.ValuedConstraint;
import me.zeroeightsix.fiber.api.serialization.Marshaller;
import me.zeroeightsix.fiber.tree.ConfigTree;
import me.zeroeightsix.fiber.tree.ConfigLeaf;
import me.zeroeightsix.fiber.tree.ConfigGroup;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class SchemaGenerator {

	private HashMap<Class<?>, Identifier> classIdentifierHashMap = new HashMap<>();

	@Nullable
	private Marshaller<JsonElement> marshaller;

	public SchemaGenerator(@Nullable Marshaller<JsonElement> marshaller) {
		this.marshaller = marshaller;

		classIdentifierHashMap.put(Boolean.class, identifier("boolean"));
		classIdentifierHashMap.put(Byte.class, identifier("byte"));
		classIdentifierHashMap.put(Short.class, identifier("short"));
		classIdentifierHashMap.put(Integer.class, identifier("int"));
		classIdentifierHashMap.put(Long.class, identifier("long"));
		classIdentifierHashMap.put(Float.class, identifier("float"));
		classIdentifierHashMap.put(Double.class, identifier("double"));
		classIdentifierHashMap.put(String.class, identifier("string"));
	}

	public SchemaGenerator() {
		this(null);
	}

	public JsonObject createSchema(ConfigTree tree) {
		JsonObject object = new JsonObject();

		tree.getItems().forEach(item -> {
			// TODO: Maybe allow for custom schema deserialisers? / generic metadata
			if (item instanceof ConfigGroup) {
				object.put(item.getName(), createSchema((ConfigTree) item));
			} else if (item instanceof ConfigLeaf) {
				object.put(item.getName(), createSchema((ConfigLeaf<?>) item));
			}
		});

		return object;
	}

	private JsonObject createSchema(ConfigLeaf<?> item) {
		JsonObject object = new JsonObject();
		if (item.getType() != null && classIdentifierHashMap.containsKey(item.getType())) {
			object.put("type", new JsonPrimitive(classIdentifierHashMap.get(item.getType())));
		}
		if (item.getComment() != null) {
			object.put("comment", new JsonPrimitive(item.getComment()));
		}
		if (item.getDefaultValue() != null) {
			Object o = item.getDefaultValue();
			object.put("defaultValue", Optional.ofNullable(marshaller != null ? marshaller.marshall(o) : null).orElse(MarshallerImpl.getFallback().serialize(o)));
		}
		if (!item.getConstraints().isEmpty()) {
			object.put("constraints", createSchema(item.getConstraints()));
		}
		return object;
	}

	private JsonElement createSchema(List<? extends Constraint<?>> constraintList) {
		JsonArray array = new JsonArray();
		for (Constraint<?> constraint : constraintList) {
			JsonObject object = new JsonObject();
			object.put("identifier", new JsonPrimitive(constraint.getType().getIdentifier().toString()));
			if (constraint instanceof ValuedConstraint) {
				object.put("value", new JsonPrimitive(((ValuedConstraint<?, ?>) constraint).getValue()));
			}
			if (constraint instanceof CompositeConstraintsBuilder.AbstractCompositeConstraint<?>) {
				object.put("constraints", createSchema(((CompositeConstraintsBuilder.AbstractCompositeConstraint<?>) constraint).constraints));
			}
			array.add(object);
		}
		return array;
	}

	private Identifier identifier(String name) {
		return new Identifier("fiber", name);
	}

}
