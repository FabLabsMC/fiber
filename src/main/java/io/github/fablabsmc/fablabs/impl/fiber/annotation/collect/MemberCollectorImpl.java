package io.github.fablabsmc.fablabs.impl.fiber.annotation.collect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Listener;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.MemberCollector;
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
	public <P> void collect(P pojo, Class<? super P> clazz, SettingProcessor processor) throws ProcessingMemberException {
		for (Method m : clazz.getDeclaredMethods()) {
			if (isIncluded(m) && m.isAnnotationPresent(Listener.class)) {
				processor.processListenerMethod(pojo, m, m.getAnnotation(Listener.class).value());
			}
		}

		for (Field f : clazz.getDeclaredFields()) {
			if (isIncluded(f) && f.isAnnotationPresent(Listener.class)) {
				processor.processListenerField(pojo, f, f.getAnnotation(Listener.class).value());
			}
		}

		for (Field f : clazz.getDeclaredFields()) {
			if (isIncluded(f) && !f.isAnnotationPresent(Listener.class)) {
				if (f.isAnnotationPresent(Setting.Group.class)) {
					processor.processGroup(pojo, f);
				} else {
					processor.processSetting(pojo, f);
				}
			}
		}
	}
}
