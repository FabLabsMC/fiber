package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.RecordSerializableType;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;

import java.util.Map;

public class RecordConstraintChecker extends ConstraintChecker<ConfigBranch, RecordSerializableType> {

    private static final RecordConstraintChecker INSTANCE = new RecordConstraintChecker();

    public static RecordConstraintChecker instance() {
        return INSTANCE;
    }

    private RecordConstraintChecker() { }

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
