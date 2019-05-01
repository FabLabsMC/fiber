package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.List;

public class ConstraintsBuilder<T> extends AbstractConstraintsBuilder<T> {

	final ConfigValueBuilder<T> source;

	public ConstraintsBuilder(List<Constraint> sourceConstraints, Class<T> type, ConfigValueBuilder<T> source) {
		super(sourceConstraints, type);
		this.source = source;
	}

	public CompositeConstraintBuilder<T> composite(CompositeType type) {
		return new CompositeConstraintBuilder<>(type, sourceConstraints, this.type, this);
	}

	public ConstraintsBuilder<T> minNumerical(T min) throws RuntimeFiberException {
		addNumericalLowerBound(min);
		return this;
	}

	public ConstraintsBuilder<T> maxNumerical(T min) throws RuntimeFiberException {
		addNumericalUpperBound(min);
		return this;
	}

	public ConfigValueBuilder<T> finish() {
		super.addConstraints();
		return source;
	}

}
