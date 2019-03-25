package me.zeroeightsix.fiber.constraint;

import me.zeroeightsix.fiber.ConfigValueBuilder;

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

	public ConstraintsBuilder<T> min(T min) {
		addNumericalLowerBound(min);
		return this;
	}

	public ConstraintsBuilder<T> max(T min) {
		addNumericalUpperBound(min);
		return this;
	}

	public ConfigValueBuilder<T> finish() {
		super.addConstraints();
		return source;
	}

}
