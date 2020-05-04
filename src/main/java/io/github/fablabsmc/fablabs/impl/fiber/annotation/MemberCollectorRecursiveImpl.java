package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;

public class MemberCollectorRecursiveImpl extends MemberCollectorImpl {
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

	private <M extends Member> Set<M> collectMembersRecursively(Class<?> clazz, Function<Class<?>, M[]> collector) {
		Set<M> set = Arrays.stream(collector.apply(clazz)).filter(MemberCollectorImpl::isIncluded).collect(Collectors.toSet());
		Class<?> superClass = clazz.getSuperclass();

		if (superClass != null) {
			set.addAll(collectMembersRecursively(superClass, collector));
		}

		return set;
	}
}
