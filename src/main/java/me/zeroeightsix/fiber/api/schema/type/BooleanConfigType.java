package me.zeroeightsix.fiber.api.schema.type;


import me.zeroeightsix.fiber.api.serialization.TypeSerializer;
import me.zeroeightsix.fiber.impl.constraint.BooleanTypeChecker;
import me.zeroeightsix.fiber.impl.constraint.Constraint;

public final class BooleanConfigType extends ConfigType<Boolean> {
    public static final BooleanConfigType BOOLEAN = new BooleanConfigType();
    private final BooleanTypeChecker constraint;

    private BooleanConfigType() {
        super(Boolean.class);
        this.constraint = new BooleanTypeChecker(this);
    }

    @Override
    public <S> void serialize(TypeSerializer<S> serializer, S target) {
        serializer.serialize(this, target);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BooleanConfigType;
    }

    @Override
    public int hashCode() {
        return 1337;
    }

    @Override
    protected Constraint<Boolean, ?> getConstraint() {
        return this.constraint;
    }
}
