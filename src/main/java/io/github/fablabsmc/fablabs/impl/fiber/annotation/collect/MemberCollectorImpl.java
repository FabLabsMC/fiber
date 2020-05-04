package io.github.fablabsmc.fablabs.impl.fiber.annotation.collect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Listener;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.ListenerProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.SettingProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;

public class MemberCollectorImpl implements MemberCollector {
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

	@Override
	public <P> void collectListeners(P pojo, Class<P> clazz, ListenerProcessor processor) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (isIncluded(m) && m.isAnnotationPresent(Listener.class)) {
				processor.processMethod(m, m.getAnnotation(Listener.class).value());
			}
		}

		for (Field f : clazz.getDeclaredFields()) {
			if (isIncluded(f) && f.isAnnotationPresent(Listener.class)) {
				processor.processField(f, f.getAnnotation(Listener.class).value());
			}
		}
	}

	@Override
	public <P> void collectSettings(P pojo, Class<P> clazz, SettingProcessor processor) throws ProcessingMemberException {
		for (Field f : clazz.getDeclaredFields()) {
			if (isIncluded(f) && !f.isAnnotationPresent(Listener.class)) {
				if (f.isAnnotationPresent(Setting.Group.class)) {
					processor.processGroup(f);
				} else {
					processor.processSetting(f);
				}
			}
		}
	}
}
