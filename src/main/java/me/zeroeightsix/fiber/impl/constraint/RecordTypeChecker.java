package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.constraint.TypeCheckResult;
import me.zeroeightsix.fiber.api.schema.type.ConfigType;
import me.zeroeightsix.fiber.api.schema.type.RecordConfigType;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;

import java.util.Map;

public class RecordTypeChecker extends Constraint<ConfigBranch, RecordConfigType> {

    public RecordTypeChecker(RecordConfigType cfg) {
        super(cfg);
    }

    @Override
    public TypeCheckResult<ConfigBranch> test(ConfigBranch value) {
        for (Map.Entry<String, ConfigType<?>> field : this.cfg.getFields().entrySet()) {
            ConfigNode child = value.lookup(field.getKey());
            if (!(child instanceof ConfigLeaf)) {
                return TypeCheckResult.unrecoverable();
            }
            ConfigType<?> leafType = ((ConfigLeaf<?>) child).getConfigType();
            ConfigType<?> fieldType = field.getValue();
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
        for (Map.Entry<String, ConfigType<?>> entry : this.cfg.getFields().entrySet()) {
            ConfigType<?> other = that.cfg.getFields().get(entry.getKey());
            if (!entry.getValue().equals(other)) {
                return false;
            }
        }
        return true;
    }
}
