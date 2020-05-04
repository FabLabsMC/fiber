package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Listener;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;

public class MemberCollectorImpl implements MemberCollector<ConfigTreeBuilder> {
	@Override
	public Set<Field> collectFields(Object pojo, ConfigTreeBuilder builder) {
		return Arrays.stream(pojo.getClass().getDeclaredFields())
				.filter(MemberCollectorImpl::isIncluded)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Method> collectMethods(Object pojo, ConfigTreeBuilder builder) {
		return Arrays.stream(pojo.getClass().getDeclaredMethods())
				.filter(MemberCollectorImpl::isIncluded)
				.collect(Collectors.toSet());
	}

	public static boolean isIncluded(Member member) {
		if (member.isSynthetic() || Modifier.isTransient(member.getModifiers())) return false;

		boolean onlyAnnotated = false; // Assume defaults, see Settings annotation

		Class<?> owningClass = member.getDeclaringClass();

		if (owningClass.isAnnotationPresent(Settings.class)) {
			onlyAnnotated = owningClass.getAnnotation(Settings.class).onlyAnnotated();
		}

		if (member instanceof AccessibleObject) {
			AccessibleObject object = (AccessibleObject) member;

			if (object.isAnnotationPresent(Setting.class)) {
				return !object.getAnnotation(Setting.class).ignore();
			}

			return object.isAnnotationPresent(Listener.class) || !onlyAnnotated;
		}

		return false;
	}
}
