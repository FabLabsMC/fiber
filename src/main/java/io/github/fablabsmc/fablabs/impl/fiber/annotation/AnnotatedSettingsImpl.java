package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.NodeOperations;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.PojoMemberProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.BranchAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConfigAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.LeafAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ParameterizedTypeProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigLeafBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberTypeProcessingException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.MalformedFieldException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.RuntimeFiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.magic.TypeMagic;

public final class AnnotatedSettingsImpl implements AnnotatedSettings {
	private final Map<Class<?>, ParameterizedTypeProcessor<?>> registeredGenericTypes;
	private final Map<Class<?>, ConfigType<?, ?, ?>> registeredTypes;
	private final Map<Class<? extends Annotation>, LeafAnnotationProcessor<?>> valueSettingProcessors;
	private final Map<Class<? extends Annotation>, BranchAnnotationProcessor<?>> groupSettingProcessors;
	private final Map<Class<? extends Annotation>, ConstraintAnnotationProcessor<?>> constraintProcessors;
	private final MemberCollector memberCollector;
	private final SettingNamingConvention convention;

	AnnotatedSettingsImpl(Map<Class<?>, ParameterizedTypeProcessor<?>> registeredGenericTypes, Map<Class<?>, ConfigType<?, ?, ?>> registeredTypes, Map<Class<? extends Annotation>, LeafAnnotationProcessor<?>> valueSettingProcessors, Map<Class<? extends Annotation>, BranchAnnotationProcessor<?>> groupSettingProcessors, Map<Class<? extends Annotation>, ConstraintAnnotationProcessor<?>> constraintProcessors, MemberCollector memberCollector, SettingNamingConvention convention) {
		this.registeredGenericTypes = Collections.unmodifiableMap(new LinkedHashMap<>(registeredGenericTypes));
		this.registeredTypes = Collections.unmodifiableMap(new LinkedHashMap<>(registeredTypes));
		this.valueSettingProcessors = Collections.unmodifiableMap(new LinkedHashMap<>(valueSettingProcessors));
		this.groupSettingProcessors = Collections.unmodifiableMap(new LinkedHashMap<>(groupSettingProcessors));
		this.constraintProcessors = Collections.unmodifiableMap(new LinkedHashMap<>(constraintProcessors));
		this.memberCollector = memberCollector;
		this.convention = convention;
	}

	public ConfigBranch makeTree(Object pojo) throws FiberException {
		ConfigTreeBuilder builder = ConfigTree.builder();
		this.applyToNode(builder, pojo);
		return builder.build();
	}

	public <P> void applyToNode(ConfigTree mergeTo, P pojo) throws FiberException {
		@SuppressWarnings("unchecked") Class<P> pojoClass = (Class<P>) pojo.getClass();
		SettingNamingConvention convention = findSettingAnnotation(Settings.class, pojoClass)
				.map(Settings::namingConvention)
				.map(AnnotatedSettingsImpl::createConvention)
				.orElse(this.convention);
		ConfigTreeBuilder builder = ConfigTree.builder();
		PojoMemberProcessorImpl processor = this.new PojoMemberProcessorImpl(convention, builder);
		this.memberCollector.collect(pojo, pojoClass, processor);
		NodeOperations.moveChildren(builder, mergeTo);
	}

	private static void checkViolation(Field field) throws FiberException {
		if (Modifier.isFinal(field.getModifiers())) {
			throw new FiberException("Field '" + field.getName() + "' can not be final");
		}
	}

	private static <A extends Annotation> Optional<A> findSettingAnnotation(Class<A> annotationType, AnnotatedElement field) {
		return Optional.ofNullable(field.getAnnotation(annotationType));
	}

	private static SettingNamingConvention createConvention(Class<? extends SettingNamingConvention> namingConvention) {
		try {
			return namingConvention.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeFiberException("Could not initialise naming convention", e);
		}
	}

	private class PojoMemberProcessorImpl implements PojoMemberProcessor {
		private final SettingNamingConvention convention;
		private final Map<String, List<Member>> listenerMap = new HashMap<>();
		private final ConfigTreeBuilder builder;

		PojoMemberProcessorImpl(SettingNamingConvention convention, ConfigTreeBuilder builder) {
			this.convention = convention;
			this.builder = builder;
		}

		@Override
		public void processListenerMethod(Object pojo, Method method, String name) {
			this.listenerMap.computeIfAbsent(name, v -> new ArrayList<>()).add(method);
		}

		@Override
		public void processListenerField(Object pojo, Field field, String name) {
			this.listenerMap.computeIfAbsent(name, v -> new ArrayList<>()).add(field);
		}

		@Override
		public void processGroup(Object pojo, Field group) throws ProcessingMemberException {
			try {
				String name = this.findName(group);
				ConfigTreeBuilder sub = this.builder.fork(name);
				group.setAccessible(true);
				Object subPojo = group.get(pojo);

				if (subPojo == null) {
					throw new ProcessingMemberException("Group " + name + " is null. Did you forget to initialize it?", group);
				}

				AnnotatedSettingsImpl.this.applyToNode(sub, subPojo);
				this.applyAnnotationProcessors(pojo, group, sub, AnnotatedSettingsImpl.this.groupSettingProcessors);
				sub.build();
			} catch (FiberException | IllegalAccessException e) {
				throw new ProcessingMemberException("Failed to process group '" + Modifier.toString(group.getModifiers()) + " " + group.getType().getSimpleName() + " " + group.getName() + "' in " + group.getDeclaringClass().getSimpleName(), e, group);
			}
		}

		@Override
		public void processSetting(Object pojo, Field setting) throws ProcessingMemberException {
			try {
				checkViolation(setting);
				this.processSetting(pojo, setting, this.toConfigType(setting.getAnnotatedType()));
			} catch (FiberException e) {
				throw new ProcessingMemberException("Failed to process setting '" + Modifier.toString(setting.getModifiers()) + " " + setting.getType().getSimpleName() + " " + setting.getName() + "' in " + setting.getDeclaringClass().getSimpleName(), e, setting);
			}
		}

		private <R, S> void processSetting(Object pojo, Field setting, ConfigType<R, S, ?> type) throws FiberException {
			String name = this.findName(setting);
			List<Member> listeners = this.listenerMap.getOrDefault(name, Collections.emptyList());
			ConfigLeafBuilder<S, R> leaf = this.builder
					.beginValue(name, type, this.findDefaultValue(pojo, setting))
					.withComment(this.findComment(setting))
					.withListener(this.constructListener(pojo, setting, listeners, type));
			this.applyAnnotationProcessors(pojo, setting, leaf, AnnotatedSettingsImpl.this.valueSettingProcessors);
			leaf.build();
		}

		@Nonnull
		private String findName(Field field) {
			return findSettingAnnotation(Setting.Group.class, field).map(Setting.Group::name).filter(s -> !s.isEmpty()).orElseGet(
					() -> findSettingAnnotation(Setting.class, field).map(Setting::name).filter(s -> !s.isEmpty()).orElseGet(
							() -> this.convention.name(field.getName())
					)
			);
		}

		@Nullable
		private String findComment(Field field) {
			return findSettingAnnotation(Setting.class, field).map(Setting::comment).filter(s -> !s.isEmpty()).orElse(null);
		}

		@Nonnull
		private <R> BiConsumer<R, R> constructListener(Object pojo, Field setting, List<Member> listeners, ConfigType<R, ?, ?> type) throws FiberException {
			BiConsumer<R, R> ret = (t, newValue) -> {
				try {
					setting.setAccessible(true);
					setting.set(pojo, newValue);
				} catch (IllegalAccessException e) {
					throw new RuntimeFiberException("Failed to update field value", e);
				}
			};

			for (Member listener : listeners) {
				BiConsumer<R, R> consumer = this.constructListenerFromMember(pojo, listener, type.getRuntimeType());
				if (consumer != null) ret = ret.andThen(consumer);
			}

			return ret;
		}

		private <C> void applyAnnotationProcessors(Object pojo, Field field, C sub, Map<Class<? extends Annotation>, ? extends ConfigAnnotationProcessor<?, Field, C>> settingProcessors) {
			for (Annotation annotation : field.getAnnotations()) {
				@SuppressWarnings("unchecked") ConfigAnnotationProcessor<Annotation, Field, C> processor = (ConfigAnnotationProcessor<Annotation, Field, C>) settingProcessors.get(annotation.annotationType());

				if (processor != null) {
					processor.apply(annotation, field, pojo, sub);
				}
			}
		}

		@Nonnull
		private ConfigType<?, ?, ?> toConfigType(AnnotatedType annotatedType) throws FiberTypeProcessingException {
			Class<?> clazz = TypeMagic.classForType(annotatedType.getType());

			if (clazz == null) {
				throw new FiberTypeProcessingException("Unknown type " + annotatedType.getType().getTypeName());
			}

			@Nonnull ConfigType<?, ?, ?> ret;

			if (annotatedType instanceof AnnotatedArrayType) {
				ConfigType<?, ?, ?> componentType = this.toConfigType(((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType());
				Class<?> componentClass = clazz.getComponentType();
				assert componentClass != null;
				ret = this.makeArrayConfigType(componentClass, componentType);
			} else if (AnnotatedSettingsImpl.this.registeredGenericTypes.containsKey(clazz)) {
				ParameterizedTypeProcessor<?> parameterizedTypeProcessor = AnnotatedSettingsImpl.this.registeredGenericTypes.get(clazz);

				if (!(annotatedType instanceof AnnotatedParameterizedType)) {
					throw new FiberTypeProcessingException("Expected type parameters for " + clazz);
				}

				AnnotatedType[] annotatedTypeArgs = ((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments();
				ConfigType<?, ?, ?>[] typeArguments = new ConfigType[annotatedTypeArgs.length];

				for (int i = 0; i < annotatedTypeArgs.length; i++) {
					typeArguments[i] = this.toConfigType(annotatedTypeArgs[i]);
				}

				ret = parameterizedTypeProcessor.process(typeArguments);
			} else if (AnnotatedSettingsImpl.this.registeredTypes.containsKey(clazz)) {
				ret = AnnotatedSettingsImpl.this.registeredTypes.get(clazz);
			} else if (clazz.isEnum()) {
				ret = ConfigTypes.makeEnum(clazz.asSubclass(Enum.class));
			} else {
				Optional<Class<?>> closestParent = Stream.concat(AnnotatedSettingsImpl.this.registeredGenericTypes.keySet().stream(), AnnotatedSettingsImpl.this.registeredTypes.keySet().stream())
						.filter(c -> c.isAssignableFrom(clazz))
						.reduce((c1, c2) -> c1.isAssignableFrom(c2) ? c2 : c1);
				String closestParentSuggestion = closestParent.map(p -> "declaring the element as '" + p.getTypeName() + "', or ").orElse("");
				throw new FiberTypeProcessingException("Unknown config type " + annotatedType.getType().getTypeName()
						+ ". Consider marking as transient, or " + closestParentSuggestion + "registering a new Class -> ConfigType mapping.");
			}

			assert ret != null;
			return this.constrain(ret, annotatedType);
		}

		@SuppressWarnings("unchecked")
		private ConfigType<?, ?, ?> makeArrayConfigType(Class<?> componentClass, ConfigType<?, ?, ?> componentType) {
			assert TypeMagic.wrapPrimitive(componentClass) == TypeMagic.wrapPrimitive(componentType.getRuntimeType()) : "Class=" + componentClass + ", ConfigType=" + componentType;

			if (componentClass == boolean.class) {
				return ConfigTypes.makeBooleanArray((ConfigType<Boolean, ?, ?>) componentType);
			} else if (componentClass == byte.class) {
				return ConfigTypes.makeByteArray((ConfigType<Byte, ?, ?>) componentType);
			} else if (componentClass == short.class) {
				return ConfigTypes.makeShortArray((ConfigType<Short, ?, ?>) componentType);
			} else if (componentClass == int.class) {
				return ConfigTypes.makeIntArray((ConfigType<Integer, ?, ?>) componentType);
			} else if (componentClass == long.class) {
				return ConfigTypes.makeLongArray((ConfigType<Long, ?, ?>) componentType);
			} else if (componentClass == float.class) {
				return ConfigTypes.makeFloatArray((ConfigType<Float, ?, ?>) componentType);
			} else if (componentClass == double.class) {
				return ConfigTypes.makeDoubleArray((ConfigType<Double, ?, ?>) componentType);
			} else if (componentClass == char.class) {
				return ConfigTypes.makeCharArray((ConfigType<Character, ?, ?>) componentType);
			} else {
				assert !componentClass.isPrimitive() : "Primitive component type: " + componentClass;
				return ConfigTypes.makeArray(componentType);
			}
		}

		private <T extends ConfigType<?, ?, ?>> T constrain(T type, AnnotatedElement annotated) throws FiberTypeProcessingException {
			T ret = type;

			for (Annotation annotation : annotated.getAnnotations()) {
				@SuppressWarnings("unchecked") ConstraintAnnotationProcessor<Annotation> processor =
						(ConstraintAnnotationProcessor<Annotation>) AnnotatedSettingsImpl.this.constraintProcessors.get(annotation.annotationType());
				if (processor != null) {
					try {
						ret = this.constrain(ret, processor, annotation, annotated);
					} catch (UnsupportedOperationException e) {
						throw new FiberTypeProcessingException("Failed to constrain type " + type, e);
					}
				}
			}

			return ret;
		}

		@SuppressWarnings("unchecked")
		private <T extends ConfigType<?, ?, ?>> T constrain(T type, ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
			return (T) type.constrain(processor, annotation, annotated);
		}

		@SuppressWarnings("unchecked")
		private <T> T findDefaultValue(Object pojo, Field field) throws FiberException {
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			T value;

			try {
				value = (T) field.get(pojo);

				if (value == null) {
					throw new MalformedFieldException("Default value for field '" + field.getName() + "' is null");
				}
			} catch (IllegalAccessException e) {
				throw new FiberException("Couldn't get value for field '" + field.getName() + "'", e);
			}

			field.setAccessible(accessible);
			return value;
		}

		private <T> BiConsumer<T, T> constructListenerFromMember(Object pojo, Member listener, Class<T> wantedType) throws FiberException {
			BiConsumer<T, T> result;

			if (listener instanceof Field) {
				result = this.constructListenerFromField(pojo, (Field) listener, wantedType);
			} else if (listener instanceof Method) {
				result = this.constructListenerFromMethod(pojo, (Method) listener, wantedType);
			} else {
				throw new FiberException("Cannot create listener from " + listener + ": must be a field or method");
			}

			// note: we assume that a value coming from the IR is valid
			return result;
		}

		private <T, A> BiConsumer<T, T> constructListenerFromMethod(Object pojo, Method method, Class<A> wantedType) throws FiberException {
			int i = this.checkListenerMethod(method, wantedType);
			method.setAccessible(true);
			final boolean staticMethod = Modifier.isStatic(method.getModifiers());
			switch (i) {
			case 1:
				return (oldValue, newValue) -> {
					try {
						method.invoke(staticMethod ? null : pojo, newValue);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeFiberException("Failed to invoke listener " + method + " with argument " + newValue, e);
					}
				};
			case 2:
				return (oldValue, newValue) -> {
					try {
						method.invoke(staticMethod ? null : pojo, oldValue, newValue);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeFiberException("Failed to invoke listener " + method + " with arguments " + oldValue + ", " + newValue, e);
					}
				};
			default:
				throw new FiberException("Listener failed due to an invalid number of arguments.");
			}
		}

		private <A> int checkListenerMethod(Method method, Class<A> wantedType) throws FiberException {
			if (!method.getReturnType().equals(void.class)) {
				throw new FiberException("Listener method must return void");
			}

			int paramCount = method.getParameterCount();

			if ((paramCount != 1 && paramCount != 2) || !method.getParameterTypes()[0].equals(wantedType)) {
				throw new FiberException("Listener method must have exactly two parameters of type that it listens for");
			}

			return paramCount;
		}

		private <T, A> BiConsumer<T, T> constructListenerFromField(Object pojo, Field field, Class<A> wantedType) throws FiberException {
			this.checkListenerField(field, wantedType);
			field.setAccessible(true);

			try {
				@SuppressWarnings("unchecked") BiConsumer<T, T> consumer = (BiConsumer<T, T>) field.get(pojo);
				return consumer;
			} catch (IllegalAccessException e) {
				throw new FiberException("Could not construct listener", e);
			}
		}

		private <A> void checkListenerField(Field field, Class<A> wantedType) throws MalformedFieldException {
			if (!field.getType().equals(BiConsumer.class)) {
				throw new MalformedFieldException("Field " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must be a BiConsumer");
			}

			ParameterizedType genericTypes = (ParameterizedType) field.getGenericType();

			if (genericTypes.getActualTypeArguments().length != 2) {
				throw new MalformedFieldException("Listener " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must have 2 generic types");
			} else if (genericTypes.getActualTypeArguments()[0] != genericTypes.getActualTypeArguments()[1]) {
				throw new MalformedFieldException("Listener " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must have 2 identical generic types");
			} else if (!genericTypes.getActualTypeArguments()[0].equals(wantedType)) {
				throw new MalformedFieldException("Listener " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must have the same generic type as the field it's listening for");
			}
		}
	}
}
