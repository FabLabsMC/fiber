package me.zeroeightsix.fiber.builder.constraint;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.constraint.CompositeType;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;

import javax.annotation.RegEx;
import java.util.List;
import java.util.regex.Pattern;

public final class ConstraintsBuilder<T> extends AbstractConstraintsBuilder<T, ConstraintsBuilder<T>> {

	final ConfigValueBuilder<T> source;

	public ConstraintsBuilder(List<Constraint<? super T>> sourceConstraints, Class<T> type, ConfigValueBuilder<T> source) {
		super(sourceConstraints, type);
		this.source = source;
	}

	public CompositeConstraintBuilder<T> composite(CompositeType type) {
		return new CompositeConstraintBuilder<>(type, sourceConstraints, this.type, this);
	}

	public ConfigValueBuilder<T> finish() {
		super.addConstraints();
		return source;
	}
}
