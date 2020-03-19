package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;
import me.zeroeightsix.fiber.constraint.ValuedConstraint;

import java.util.List;

public final class CompositeConstraintBuilder<S, T> extends AbstractConstraintsBuilder<S, T, T, CompositeConstraintBuilder<S, T>> {

	private final CompositeType compositeType;

	public CompositeConstraintBuilder(S source, CompositeType compositeType, List<Constraint<? super T>> sourceConstraints, Class<T> type) {
		super(source, sourceConstraints, type);
		this.compositeType = compositeType;
	}

	public S finishComposite() {
		this.sourceConstraints.add(createConstraint(newConstraints));
		return source;
	}

	private AbstractCompositeConstraint<T> createConstraint(List<Constraint<? super T>> constraints) {
		switch (compositeType) {
			case OR:
				return new OrCompositeConstraint<>(constraints);
			case AND:
				return new AndCompositeConstraint<>(constraints);
			case INVERT:
				return new InvertCompositeConstraint<>(constraints);
			default:
				throw new AssertionError();
		}
	}

	public static abstract class AbstractCompositeConstraint<T> extends ValuedConstraint<String, T> {

		public final List<Constraint<? super T>> constraints;

		public AbstractCompositeConstraint(CompositeType type, List<Constraint<? super T>> constraints) {
			super(ConstraintType.COMPOSITE, type.getName());
			this.constraints = constraints;
		}

	}

	private static final class AndCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

		public AndCompositeConstraint(List<Constraint<? super T>> constraints) {
			super(CompositeType.AND, constraints);
		}

		@Override
		public boolean test(T value) {
			return constraints.stream().allMatch(constraint -> constraint.test(value));
		}

	}

	private static final class OrCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

		public OrCompositeConstraint(List<Constraint<? super T>> constraints) {
			super(CompositeType.OR, constraints);
		}

		@Override
		public boolean test(T value) {
			return constraints.stream().anyMatch(constraint -> constraint.test(value));
		}

	}

	private static final class InvertCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

		public InvertCompositeConstraint(List<Constraint<? super T>> constraints) {
			super(CompositeType.INVERT, constraints);
		}

		@Override
		public boolean test(T value) {
			return constraints.stream().noneMatch(constraint -> constraint.test(value));
		}

	}

}
