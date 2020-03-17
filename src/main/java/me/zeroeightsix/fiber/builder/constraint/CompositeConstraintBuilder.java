package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.Constraints;
import me.zeroeightsix.fiber.constraint.ValuedConstraint;

import javax.annotation.RegEx;
import java.util.List;

public final class CompositeConstraintBuilder<T> extends AbstractConstraintsBuilder<T, CompositeConstraintBuilder<T>> {

	private final ConstraintsBuilder<T> source;
	private final CompositeType compositeType;

	public CompositeConstraintBuilder(CompositeType compositeType, List<Constraint<? super T>> sourceConstraints, Class<T> type, ConstraintsBuilder<T> source) {
		super(sourceConstraints, type);
		this.source = source;
		this.compositeType = compositeType;
	}

	public ConstraintsBuilder<T> finishComposite() {
		addConstraints();
		return source;
	}

	@Override
	void addConstraints() {
		switch (compositeType) {
			case OR:
				sourceConstraints.add(new OrCompositeConstraint<>(newConstraints));
				break;
			case AND:
				sourceConstraints.add(new AndCompositeConstraint<>(newConstraints));
				break;
			case INVERT:
				sourceConstraints.add(new InvertCompositeConstraint<>(newConstraints));
				break;
		}
	}

	public static abstract class AbstractCompositeConstraint<T> extends ValuedConstraint<String, T> {

		public final List<Constraint<? super T>> constraints;

		public AbstractCompositeConstraint(CompositeType type, List<Constraint<? super T>> constraints) {
			super(Constraints.COMPOSITE, type.getName());
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
