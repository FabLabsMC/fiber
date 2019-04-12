package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.builder.constraint.ConstraintsBuilder;
import me.zeroeightsix.fiber.tree.ConfigValueOld;
import me.zeroeightsix.fiber.tree.ConfigNodeOld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ConfigValueOldBuilder<T> {

	final Class<T> type;

	T value;
	String comment = "";
	private List<BiConsumer<T, T>> consumers = new ArrayList<>();
	private List<Constraint> constraints = new ArrayList<>();
	private String name;
	private ConfigNodeOld node;
	private boolean isFinal = false;

	public ConfigValueOldBuilder(ConfigNodeOld node, Class<T> type) {
		this.node = node;
		this.type = type;
	}

	public ConfigValueOldBuilder<T> comment(String comment) {
		if (comment == null) return this;
		if (!this.comment.isEmpty()) this.comment += "\n";
		this.comment += comment;
		return this;
	}

	public ConfigValueOldBuilder<T> listen(BiConsumer<T, T> consumer) {
		if (consumer != null) {
			consumers.add(consumer);
		}
		return this;
	}

	public ConfigValueOldBuilder<T> name(String name) {
		this.name = name;
		return this;
	}

	public ConfigValueOldBuilder<T> defaultValue(T value) {
		this.value = value;
		return this;
	}

	public ConfigValueOldBuilder<T> setFinal() {
		this.isFinal = true;
		return this;
	}

	public ConstraintsBuilder<T> constraints() {
		return new ConstraintsBuilder<>(constraints, type, this);
	}

	public ConfigValueOld<T> build() {
		return registerAndSet(new ConfigValueOld<>(comment, name, (a, b) -> consumers.forEach(consumer -> consumer.accept(a, b)), buildRestriction(), value, type, this.constraints));
	}

	private ConfigValueOld<T> registerAndSet(ConfigValueOld<T> configValue) {
		if (configValue.getName() != null) {
			node.registerAndRecover(configValue);
		}
		return configValue;
	}

	protected Predicate<T> buildRestriction() {
		return isFinal ? t -> true : t -> constraints.stream().anyMatch(constraint -> constraint.test(t));
		//return isFinal ? t -> true : (restrictions.isEmpty() ? t -> false : t -> restrictions.stream().anyMatch(function -> !function.apply(t)));
	}
}
