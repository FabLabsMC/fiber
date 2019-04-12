package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.exceptions.RuntimeFiberException;
import me.zeroeightsix.fiber.builder.ConfigValueOldBuilder;
import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.List;

public class ConstraintsBuilder<T> extends AbstractConstraintsBuilder<T> {

	final ConfigValueOldBuilder<T> source;

	public ConstraintsBuilder(List<Constraint> sourceConstraints, Class<T> type, ConfigValueOldBuilder<T> source) {
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

	public ConfigValueOldBuilder<T> finish() {
		super.addConstraints();
		return source;
	}

}
