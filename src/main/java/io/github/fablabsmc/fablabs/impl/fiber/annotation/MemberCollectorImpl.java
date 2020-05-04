package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;

public class MemberCollectorImpl implements MemberCollector<ConfigTreeBuilder> {
	@Override
	public Set<Field> collectFields(Object pojo, ConfigTreeBuilder builder) {
		return new HashSet<>(Arrays.asList(pojo.getClass().getDeclaredFields()));
	}

	@Override
	public Set<Method> collectMethods(Object pojo, ConfigTreeBuilder builder) {
		return new HashSet<>(Arrays.asList(pojo.getClass().getDeclaredMethods()));
	}
}
