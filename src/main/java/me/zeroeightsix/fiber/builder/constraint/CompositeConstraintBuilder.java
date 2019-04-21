package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.Constraints;
import me.zeroeightsix.fiber.constraint.ValuedConstraint;

import java.util.List;

public final class CompositeConstraintBuilder<T> extends AbstractConstraintsBuilder<T> {

	private final ConstraintsBuilder<T> source;
	private final CompositeType compositeType;

	public CompositeConstraintBuilder(CompositeType compositeType, List<Constraint> sourceConstraints, Class<T> type, ConstraintsBuilder<T> source) {
		super(sourceConstraints, type);
		this.source = source;
		this.compositeType = compositeType;
	}

	public CompositeConstraintBuilder<T> minNumerical(T min) {
		addNumericalLowerBound(min);
		return this;
	}

	public CompositeConstraintBuilder<T> maxNumerical(T min) {
		addNumericalUpperBound(min);
		return this;
	}

	public ConstraintsBuilder<T> finishComposite() {
		addConstraints();
		return source;
	}

	@Override
	void addConstraints() {
		switch (compositeType) {
			case OR:
				sourceConstraints.add(new OrCompositeConstraint(newConstraints));
				break;
			case AND:
				sourceConstraints.add(new AndCompositeConstraint(newConstraints));
				break;
			case INVERT:
				sourceConstraints.add(new InvertCompositeConstraint(newConstraints));
				break;
		}
	}

	public abstract class AbstractCompositeConstraint<T> extends ValuedConstraint<String, T> {

		public final List<Constraint> constraints;

		public AbstractCompositeConstraint(CompositeType type, List<Constraint> constraints) {
			super(Constraints.COMPOSITE, type.getName());
			this.constraints = constraints;
		}

	}

	private class AndCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

		public AndCompositeConstraint(List<Constraint> constraints) {
			super(CompositeType.AND, constraints);
		}

		@Override
		public boolean test(T value) {
			return constraints.stream().anyMatch(constraint -> constraint.test(value));
		}

	}

	private class OrCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

		public OrCompositeConstraint(List<Constraint> constraints) {
			super(CompositeType.OR, constraints);
		}

		@Override
		public boolean test(T value) {
			return constraints.stream().anyMatch(constraint -> constraint.test(value));
		}

	}

	private class InvertCompositeConstraint<T> extends AbstractCompositeConstraint<T> {

		public InvertCompositeConstraint(List<Constraint> constraints) {
			super(CompositeType.INVERT, constraints);
		}

		@Override
		public boolean test(T value) {
			return !constraints.stream().anyMatch(constraint -> constraint.test(value));
		}

	}

}
