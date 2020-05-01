package io.github.fablabsmc.fablabs.impl.fiber.constraint;

import java.util.Map;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.TypeCheckResult;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigNode;

public class RecordConstraintChecker extends ConstraintChecker<ConfigBranch, RecordSerializableType> {
	private static final RecordConstraintChecker INSTANCE = new RecordConstraintChecker();

	public static RecordConstraintChecker instance() {
		return INSTANCE;
	}

	private RecordConstraintChecker() {
	}

	@Override
	public TypeCheckResult<ConfigBranch> test(RecordSerializableType cfg, ConfigBranch value) {
		for (Map.Entry<String, SerializableType<?>> field : cfg.getFields().entrySet()) {
			ConfigNode child = value.lookup(field.getKey());

			if (!(child instanceof ConfigLeaf)) {
				return TypeCheckResult.unrecoverable();
			}

			SerializableType<?> leafType = ((ConfigLeaf<?>) child).getConfigType();
			SerializableType<?> fieldType = field.getValue();

			if (!fieldType.isAssignableFrom(leafType)) {
				return TypeCheckResult.unrecoverable();
			}
		}

		return TypeCheckResult.successful(value);
	}

	@Override
	public boolean comprehends(RecordSerializableType cfg, RecordSerializableType cfg2) {
		for (Map.Entry<String, SerializableType<?>> entry : cfg.getFields().entrySet()) {
			SerializableType<?> other = cfg2.getFields().get(entry.getKey());

			if (!entry.getValue().equals(other)) {
				return false;
			}
		}

		return true;
	}
}
