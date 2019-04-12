package me.zeroeightsix.fiber;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import me.zeroeightsix.fiber.builder.constraint.CompositeConstraintBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ValuedConstraint;
import me.zeroeightsix.fiber.tree.*;

import java.util.List;

public class Schemas {

	public static JsonObject createSchema(Node node) {
		JsonObject object = new JsonObject();

		node.getItems().forEach(item -> {
			// TODO: Maybe allow for custom schema deserialisers? / generic metadata
			if (item instanceof Node) {
				object.put(item.getName(), createSchema((Node) item));
				return;
			} else if (item instanceof ConfigValue) {
				object.put(item.getName(), createSchema((ConfigValue) item));
			}
		});

		return object;
	}

	private static JsonObject createSchema(ConfigValue item) {
		JsonObject object = new JsonObject();
		if (item.getComment() != null) {
			object.put("comment", new JsonPrimitive(item.getComment()));
		}
		// TODO: More info
		return object;
	}

	private static JsonElement createSchema(List<Constraint> constraintList) {
		JsonArray array = new JsonArray();
		for (Constraint constraint : constraintList) {
			JsonObject object = new JsonObject();
			object.put("identifier", new JsonPrimitive(constraint.getType().getIdentifier().toString()));
			if (constraint instanceof ValuedConstraint) {
				object.put("value", new JsonPrimitive(((ValuedConstraint) constraint).getValue()));
			}
			if (constraint instanceof CompositeConstraintBuilder.AbstractCompositeConstraint) {
				object.put("constraints", createSchema(((CompositeConstraintBuilder.AbstractCompositeConstraint) constraint).constraints));
			}
			array.add(object);
		}
		return array;
	}

}
