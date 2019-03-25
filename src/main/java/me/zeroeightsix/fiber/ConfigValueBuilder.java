package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintsBuilder;
import me.zeroeightsix.fiber.ir.ConfigValue;
import me.zeroeightsix.fiber.ir.ConfigNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfigValueBuilder<S, T> {

	Class<T> type;

	T value;
	String comment = "";
	private List<BiConsumer<T, T>> consumers = new ArrayList<>();
	private List<Constraint> constraints = new ArrayList<>();
	private String name;
	private Converter<S, T> converter;
	private ConfigNode registry;
	private boolean isFinal = false;

	public ConfigValueBuilder(ConfigNode registry, Class<T> type) {
		this.registry = registry;
		this.type = type;
	}

	/**
	 * Attempts to create a copy of given ConfigValueBuilder. Will attempt to cast everything.
	 */
	protected ConfigValueBuilder(ConfigValueBuilder<S, Object> copy, Class<T> type) {
		this(copy.registry, type);
		this.value = (T) copy.value;
		this.comment = copy.comment;
		this.consumers = copy.consumers.stream().map(consumer -> (BiConsumer<T, T>) consumer::accept).collect(Collectors.toList());
		this.name = copy.name;
		this.converter = (Converter<S, T>) copy.converter;
	}

	public <A> ConfigValueBuilder type(Class<? extends A> clazz) {
		return new ConfigValueBuilder(this, clazz);
	}

	public ConfigValueBuilder<S, T> comment(String comment) {
		if (!this.comment.isEmpty()) this.comment += "\n";
		this.comment += comment;
		return this;
	}

	public ConfigValueBuilder<S, T> listen(BiConsumer<T, T> consumer) {
		consumers.add(consumer);
		return this;
	}

	public ConfigValueBuilder<S, T> name(String name) {
		this.name = name;
		return this;
	}

	public ConfigValueBuilder<S, T> defaultValue(T value) {
		this.value = value;
		return this;
	}

	public ConfigValueBuilder<S, T> converter(Converter<S, T> converter) {
		this.converter = converter;
		return this;
	}

	public ConfigValueBuilder<S, T> setFinal() {
		this.isFinal = true;
		return this;
	}

	public ConstraintsBuilder<S, T> constraints() {
		return new ConstraintsBuilder<>(constraints, type, this);
	}

	public ConfigValue<T> build() {
		return registerAndSet(new ConfigValue<>(comment, name, (a, b) -> consumers.forEach(consumer -> consumer.accept(a, b)), buildRestriction(), value, type, converter == null ? registry.provideConverter(type) : converter, this.constraints));
	}

	private ConfigValue<T> registerAndSet(ConfigValue<T> configValue) {
		if (configValue.getName() != null) {
			registry.registerAndRecover(configValue);
		}
		return configValue;
	}

	protected Predicate<T> buildRestriction() {
		return isFinal ? t -> true : t -> constraints.stream().anyMatch(constraint -> constraint.test(t));
		//return isFinal ? t -> true : (restrictions.isEmpty() ? t -> false : t -> restrictions.stream().anyMatch(function -> !function.apply(t)));
	}
}
