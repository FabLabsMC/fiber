package me.zeroeightsix.fiber.constraint;

public final class FinalConstraint<V> extends Constraint<V> {
    private static final FinalConstraint<?> INSTANCE = new FinalConstraint<>();

    public static <V> FinalConstraint<V> instance() {
        @SuppressWarnings("unchecked")
        FinalConstraint<V> instance = (FinalConstraint<V>) INSTANCE;
        return instance;
    }

    private FinalConstraint() {
        super(ConstraintType.FINAL);
    }

    @Override
    public boolean test(V value) {
        return false;
    }
}
