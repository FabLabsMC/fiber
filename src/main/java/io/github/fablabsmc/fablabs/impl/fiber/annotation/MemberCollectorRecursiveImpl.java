package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.MemberCollector;

public class MemberCollectorRecursiveImpl implements MemberCollector<ConfigTreeBuilder> {
	@Override
	public Set<Field> collectFields(Object pojo, ConfigTreeBuilder builder) {
		Class<?> clazz = pojo.getClass();
		return collectMembersRecursively(clazz, Class::getDeclaredFields);
	}

	@Override
	public Set<Method> collectMethods(Object pojo, ConfigTreeBuilder builder) {
		Class<?> clazz = pojo.getClass();
		return collectMembersRecursively(clazz, Class::getDeclaredMethods);
	}

	public static <M extends Member> Set<M> collectMembersRecursively(Class<?> clazz, Function<Class<?>, M[]> collector) {
		Set<M> set = new HashSet<>(Arrays.asList(collector.apply(clazz)));
		Class<?> superClass = clazz.getSuperclass();

		if (superClass != null) {
			set.addAll(collectMembersRecursively(superClass, collector));
		}

		return set;
	}
}
