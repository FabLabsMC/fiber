package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.RecordSerializableType;
import me.zeroeightsix.fiber.api.schema.type.SerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;

import java.util.Map;

public class RecordTypeChecker extends Constraint<ConfigBranch, RecordSerializableType> {

    public RecordTypeChecker(RecordSerializableType cfg) {
        super(cfg);
    }

    @Override
    public TypeCheckResult<ConfigBranch> test(ConfigBranch value) {
        for (Map.Entry<String, SerializableType<?>> field : this.cfg.getFields().entrySet()) {
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
    public boolean comprehends(Constraint<?, ?> constraint) {
        if (!(constraint instanceof RecordTypeChecker)) return false;
        RecordTypeChecker that = (RecordTypeChecker) constraint;
        for (Map.Entry<String, SerializableType<?>> entry : this.cfg.getFields().entrySet()) {
            SerializableType<?> other = that.cfg.getFields().get(entry.getKey());
            if (!entry.getValue().equals(other)) {
                return false;
            }
        }
        return true;
    }
}
