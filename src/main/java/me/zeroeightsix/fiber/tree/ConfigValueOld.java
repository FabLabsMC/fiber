package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.constraint.Constraint;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ConfigValueOld<T> {

	private final String comment;
	private final String name;
	private final BiConsumer<T, T> consumer;
	private final Predicate<T> restriction;
	private T value;

	private Class<T> type;

	final List<Constraint> constraintList;

	public ConfigValueOld(String comment, String name, BiConsumer<T, T> consumer, Predicate<T> restriction, T value, Class<T> type, List<Constraint> constraintList) {
		this.comment = comment;
		this.name = name;
		this.consumer = consumer;
		this.restriction = restriction;
		this.value = value;
		this.type = type;
		this.constraintList = constraintList;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	public boolean setValue(T value) {
		if (restriction.test(value)) return false;
		T oldValue = this.value;
		this.value = value;
		this.consumer.accept(oldValue, value);
		return true;
	}

	public BiConsumer<T, T> getConsumer() {
		return consumer;
	}

	public String getComment() {
		return comment;
	}

	public Class<T> getType() {
		return type;
	}

	public boolean hasComment() {
		return !comment.isEmpty();
	}

	public List<Constraint> getConstraintList() {
		return constraintList;
	}

}