package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.fablabsmc.fablabs.api.fiber.v1.NodeOperations;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Listener;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.NoNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.SettingNamingConvention;
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
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.RuntimeFiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.StringConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.magic.TypeMagic;

public final class AnnotatedSettingsImpl implements AnnotatedSettings {
	private final Map<Class<?>, ParameterizedTypeProcessor<?>> registeredGenericTypes = new HashMap<>();
	private final Map<Class<?>, ConfigType<?, ?, ?>> registeredTypes = new HashMap<>();
	private final Map<Class<? extends Annotation>, LeafAnnotationProcessor<?>> valueSettingProcessors = new HashMap<>();
	private final Map<Class<? extends Annotation>, BranchAnnotationProcessor<?>> groupSettingProcessors = new HashMap<>();
	private final Map<Class<? extends Annotation>, ConstraintAnnotationProcessor<?>> constraintProcessors = new HashMap<>();

	{
		this.registerTypeMapping(boolean.class, ConfigTypes.BOOLEAN);
		this.registerTypeMapping(Boolean.class, ConfigTypes.BOOLEAN);
		this.registerTypeMapping(byte.class, ConfigTypes.BYTE);
		this.registerTypeMapping(Byte.class, ConfigTypes.BYTE);
		this.registerTypeMapping(char.class, ConfigTypes.CHARACTER);
		this.registerTypeMapping(Character.class, ConfigTypes.CHARACTER);
		this.registerTypeMapping(short.class, ConfigTypes.SHORT);
		this.registerTypeMapping(Short.class, ConfigTypes.SHORT);
		this.registerTypeMapping(int.class, ConfigTypes.INTEGER);
		this.registerTypeMapping(Integer.class, ConfigTypes.INTEGER);
		this.registerTypeMapping(double.class, ConfigTypes.DOUBLE);
		this.registerTypeMapping(Double.class, ConfigTypes.DOUBLE);
		this.registerTypeMapping(float.class, ConfigTypes.FLOAT);
		this.registerTypeMapping(Float.class, ConfigTypes.FLOAT);
		this.registerTypeMapping(long.class, ConfigTypes.LONG);
		this.registerTypeMapping(Long.class, ConfigTypes.LONG);
		this.registerTypeMapping(String.class, ConfigTypes.STRING);
		this.registerTypeMapping(BigDecimal.class, ConfigTypes.UNBOUNDED_DECIMAL);
		this.registerTypeMapping(BigInteger.class, ConfigTypes.UNBOUNDED_INTEGER);
		this.registerTypeMapping(List.class, typeArguments -> ConfigTypes.makeList(typeArguments[0]));
		this.registerTypeMapping(Set.class, typeArguments -> ConfigTypes.makeSet(typeArguments[0]));
		this.registerGroupProcessor(Setting.Group.class, (annotation, field, pojo, node) -> {
		});
		this.registerConstraintProcessor(Setting.Constrain.Range.class, new ConstraintAnnotationProcessor<Setting.Constrain.Range>() {
			@Override
			public <T> NumberConfigType<T> processDecimal(NumberConfigType<T> baseType, Setting.Constrain.Range annotation, AnnotatedElement annotated) {
				NumberConfigType<T> ret = baseType;

				if (annotation.min() > Double.NEGATIVE_INFINITY) {
					ret = ret.withMinimum(annotation.min());
				}

				if (annotation.max() < Double.POSITIVE_INFINITY) {
					ret = ret.withMaximum(annotation.max());
				}

				if (annotation.step() > Double.MIN_VALUE) {
					ret = ret.withIncrement(annotation.step());
				}

				return ret;
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.BigRange.class, new ConstraintAnnotationProcessor<Setting.Constrain.BigRange>() {
			@Override
			public <T> NumberConfigType<T> processDecimal(NumberConfigType<T> baseType, Setting.Constrain.BigRange annotation, AnnotatedElement annotated) {
				NumberConfigType<T> ret = baseType;

				if (!annotation.min().isEmpty()) {
					ret = ret.withMinimum(new BigDecimal(annotation.min()));
				}

				if (!annotation.max().isEmpty()) {
					ret = ret.withMaximum(new BigDecimal(annotation.max()));
				}

				if (!annotation.step().isEmpty()) {
					ret = ret.withIncrement(new BigDecimal(annotation.step()));
				}

				return ret;
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.MinLength.class, new ConstraintAnnotationProcessor<Setting.Constrain.MinLength>() {
			@Override
			public <T> StringConfigType<T> processString(StringConfigType<T> baseType, Setting.Constrain.MinLength annotation, AnnotatedElement annotated) {
				return baseType.withMinLength(annotation.value());
			}

			@Override
			public <T, E> ListConfigType<T, E> processList(ListConfigType<T, E> baseType, Setting.Constrain.MinLength annotation, AnnotatedElement annotated) {
				return baseType.withMinSize(annotation.value());
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.MaxLength.class, new ConstraintAnnotationProcessor<Setting.Constrain.MaxLength>() {
			@Override
			public <T> StringConfigType<T> processString(StringConfigType<T> baseType, Setting.Constrain.MaxLength annotation, AnnotatedElement annotated) {
				return baseType.withMaxLength(annotation.value());
			}

			@Override
			public <T, E> ListConfigType<T, E> processList(ListConfigType<T, E> baseType, Setting.Constrain.MaxLength annotation, AnnotatedElement annotated) {
				return baseType.withMaxSize(annotation.value());
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.Regex.class, new ConstraintAnnotationProcessor<Setting.Constrain.Regex>() {
			@Override
			public <T> StringConfigType<T> processString(StringConfigType<T> baseType, Setting.Constrain.Regex annotation, AnnotatedElement annotated) {
				return baseType.withPattern(annotation.value());
			}
		});
	}

	@Override
	public <T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, ConfigType<T, ?, ?> type) {
		if (clazz.isArray()) throw new IllegalArgumentException("Cannot register custom mappings for arrays");

		if (this.registeredTypes.containsKey(clazz)) {
			throw new IllegalStateException(clazz + " is already linked with " + this.registeredTypes.get(clazz));
		}

		this.registeredTypes.put(clazz, type);
		return this;
	}

	@Override
	public <T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, ParameterizedTypeProcessor<T> processor) {
		if (clazz.isArray()) throw new IllegalArgumentException("Cannot register custom mappings for arrays");

		if (this.registeredGenericTypes.containsKey(clazz)) {
			throw new IllegalStateException(clazz + " is already linked with " + this.registeredGenericTypes.get(clazz));
		}

		this.registeredGenericTypes.put(clazz, processor);
		return this;
	}

	/**
	 * Registers a setting annotation processor, tasked with processing annotations on config fields.
	 *
	 * @param <A>            the type of annotation to process
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @return {@code this}, for chaining
	 */
	@Override
	public <A extends Annotation> AnnotatedSettings registerSettingProcessor(Class<A> annotationType, LeafAnnotationProcessor<A> processor) {
		if (this.valueSettingProcessors.containsKey(annotationType)) {
			throw new IllegalStateException("Cannot register multiple setting processors for the same annotation (" + annotationType + ")");
		}

		this.valueSettingProcessors.put(annotationType, processor);
		return this;
	}

	/**
	 * Registers a group annotation processor, tasked with processing annotations on ancestor fields (config fields annotated with {@link Setting.Group}.
	 *
	 * @param <A>            the type of annotation to process
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @return {@code this}, for chaining
	 */
	@Override
	public <A extends Annotation> AnnotatedSettings registerGroupProcessor(Class<A> annotationType, BranchAnnotationProcessor<A> processor) {
		if (this.groupSettingProcessors.containsKey(annotationType)) {
			throw new IllegalStateException("Cannot register multiple node processors for the same annotation (" + annotationType + ")");
		}

		this.groupSettingProcessors.put(annotationType, processor);
		return this;
	}

	/**
	 * Registers a constraint annotation processor, tasked with processing annotations on config types.
	 *
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @param <A>            the type of annotation to process
	 * @return {@code this}, for chaining
	 */
	@Override
	public <A extends Annotation> AnnotatedSettings registerConstraintProcessor(Class<A> annotationType, ConstraintAnnotationProcessor<A> processor) {
		if (this.constraintProcessors.containsKey(annotationType)) {
			throw new IllegalStateException("Cannot register multiple processors for the same annotation (" + annotationType + ")");
		}

		this.constraintProcessors.put(annotationType, processor);
		return this;
	}

	public ConfigBranch makeTree(Object pojo) throws FiberException {
		ConfigTreeBuilder builder = ConfigTree.builder();
		this.applyToNode(builder, pojo);
		return builder.build();
	}

	public <P> void applyToNode(ConfigTree mergeTo, P pojo) throws FiberException {
		@SuppressWarnings("unchecked")
		Class<P> pojoClass = (Class<P>) pojo.getClass();

		boolean onlyAnnotated;
		SettingNamingConvention convention;

		if (pojoClass.isAnnotationPresent(Settings.class)) {
			Settings settingsAnnotation = pojoClass.getAnnotation(Settings.class);
			onlyAnnotated = settingsAnnotation.onlyAnnotated();
			convention = createConvention(settingsAnnotation.namingConvention());
		} else { // Assume defaults
			onlyAnnotated = false;
			convention = new NoNamingConvention();
		}

		NodeOperations.moveChildren(this.constructNode(pojoClass, pojo, onlyAnnotated, convention), mergeTo);
	}

	private <P> ConfigTreeBuilder constructNode(Class<P> pojoClass, P pojo, boolean onlyAnnotated, SettingNamingConvention convention) throws FiberException {
		ConfigTreeBuilder node = ConfigTree.builder();

		List<Member> defaultEmpty = new ArrayList<>();
		Map<String, List<Member>> listenerMap = this.findListeners(pojoClass);

		for (Field field : pojoClass.getDeclaredFields()) {
			if (field.isSynthetic() || !isIncluded(field, onlyAnnotated)) continue;

			try {
				checkViolation(field);
				String name = findName(field, convention);

				if (field.isAnnotationPresent(Setting.Group.class)) {
					this.fieldToNode(pojo, node, field, name);
				} else {
					this.fieldToItem(node, field, pojo, name, listenerMap.getOrDefault(name, defaultEmpty));
				}
			} catch (FiberException e) {
				throw new FiberException("Failed to process field '" + Modifier.toString(field.getModifiers()) + " " + field.getType().getSimpleName() + " " + field.getName() + "' in " + pojoClass.getSimpleName(), e);
			}
		}

		return node;
	}

	private Map<String, List<Member>> findListeners(Class<?> pojoClass) {
		return Stream.concat(Arrays.stream(pojoClass.getDeclaredFields()), Arrays.stream(pojoClass.getDeclaredMethods()))
				.filter(accessibleObject -> accessibleObject.isAnnotationPresent(Listener.class))
				.collect(Collectors.groupingBy(accessibleObject -> ((AccessibleObject) accessibleObject).getAnnotation(Listener.class).value()));
	}

	private static boolean isIncluded(Field field, boolean onlyAnnotated) {
		if (isIgnored(field)) return false;
		return !onlyAnnotated || field.isAnnotationPresent(Setting.class);
	}

	private static boolean isIgnored(Field field) {
		return getSettingAnnotation(field).map(Setting::ignore).orElse(false) || Modifier.isTransient(field.getModifiers());
	}

	private static void checkViolation(Field field) throws FiberException {
		if (Modifier.isFinal(field.getModifiers())) {
			throw new FiberException("Field '" + field.getName() + "' can not be final");
		}
	}

	private static Optional<Setting> getSettingAnnotation(Field field) {
		return field.isAnnotationPresent(Setting.class) ? Optional.of(field.getAnnotation(Setting.class)) : Optional.empty();
	}

	private <P> void fieldToNode(P pojo, ConfigTreeBuilder node, Field field, String name) throws FiberException {
		ConfigTreeBuilder sub = node.fork(name);

		try {
			field.setAccessible(true);
			this.applyToNode(sub, field.get(pojo));
			this.applyAnnotationProcessors(pojo, field, sub, this.groupSettingProcessors);
			sub.build();
		} catch (IllegalAccessException e) {
			throw new FiberException("Couldn't fork and apply sub-node", e);
		}
	}

	private <S, R> void fieldToItem(ConfigTreeBuilder node, Field field, Object pojo, String name, List<Member> listeners) throws FiberException {
		@SuppressWarnings("unchecked") ConfigType<R, S, ?> type = (ConfigType<R, S, ?>) this.toConfigType(field.getAnnotatedType());

		ConfigLeafBuilder<S, R> builder = node.beginValue(name, type, this.findDefaultValue(field, pojo))
				.withComment(findComment(field));

		for (Member listener : listeners) {
			BiConsumer<R, R> consumer = this.constructListener(listener, pojo, type.getRuntimeType());
			if (consumer == null) continue;
			builder.withListener(consumer);
		}

		builder.withListener((t, newValue) -> {
			try {
				field.setAccessible(true);
				field.set(pojo, newValue);
			} catch (IllegalAccessException e) {
				throw new RuntimeFiberException("Failed to update field value", e);
			}
		});

		this.applyAnnotationProcessors(pojo, field, builder, this.valueSettingProcessors);

		builder.build();
	}

	private <C> void applyAnnotationProcessors(Object pojo, Field field, C sub, Map<Class<? extends Annotation>, ? extends ConfigAnnotationProcessor<?, Field, C>> settingProcessors) {
		for (Annotation annotation : field.getAnnotations()) {
			@SuppressWarnings("unchecked") ConfigAnnotationProcessor<Annotation, Field, C> processor = (ConfigAnnotationProcessor<Annotation, Field, C>) settingProcessors.get(annotation.annotationType());

			if (processor != null) {
				processor.apply(annotation, field, pojo, sub);
			}
		}
	}

	private ConfigType<?, ?, ?> toConfigType(AnnotatedType annotatedType) throws FiberTypeProcessingException {
		Class<?> clazz = TypeMagic.classForType(annotatedType.getType());

		if (clazz == null) {
			throw new FiberTypeProcessingException("Unknown type " + annotatedType.getType().getTypeName());
		}

		ConfigType<?, ?, ?> ret;

		if (annotatedType instanceof AnnotatedArrayType) {
			ConfigType<?, ?, ?> componentType = this.toConfigType(((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType());
			Class<?> componentClass = clazz.getComponentType();
			assert componentClass != null;
			ret = makeArrayConfigType(componentClass, componentType);
		} else if (this.registeredGenericTypes.containsKey(clazz)) {
			ParameterizedTypeProcessor<?> parameterizedTypeProcessor = this.registeredGenericTypes.get(clazz);

			if (!(annotatedType instanceof AnnotatedParameterizedType)) {
				throw new FiberTypeProcessingException("Expected type parameters for " + clazz);
			}

			AnnotatedType[] annotatedTypeArgs = ((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments();
			ConfigType<?, ?, ?>[] typeArguments = new ConfigType[annotatedTypeArgs.length];

			for (int i = 0; i < annotatedTypeArgs.length; i++) {
				typeArguments[i] = this.toConfigType(annotatedTypeArgs[i]);
			}

			ret = parameterizedTypeProcessor.process(typeArguments);
		} else {
			ret = this.registeredTypes.get(clazz);
		}

		if (ret == null) {
			Optional<Class<?>> closestParent = Stream.concat(this.registeredGenericTypes.keySet().stream(), this.registeredTypes.keySet().stream())
					.filter(c -> c.isAssignableFrom(clazz))
					.reduce((c1, c2) -> c1.isAssignableFrom(c2) ? c2 : c1);
			String closestParentSuggestion = closestParent.map(p -> "declaring the element as '" + p.getTypeName() + "', or ").orElse("");
			throw new FiberTypeProcessingException("Unknown config type " + annotatedType.getType().getTypeName()
					+ ". Consider marking as transient, or " + closestParentSuggestion + "registering a new Class -> ConfigType mapping.");
		}

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
					(ConstraintAnnotationProcessor<Annotation>) this.constraintProcessors.get(annotation.annotationType());
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
	private <T> T findDefaultValue(Field field, Object pojo) throws FiberException {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		T value;

		try {
			value = (T) field.get(pojo);
		} catch (IllegalAccessException e) {
			throw new FiberException("Couldn't get value for field '" + field.getName() + "'", e);
		}

		field.setAccessible(accessible);
		return value;
	}

	private <T> BiConsumer<T, T> constructListener(Member listener, Object pojo, Class<T> wantedType) throws FiberException {
		BiConsumer<T, T> result;

		if (listener instanceof Field) {
			result = this.constructListenerFromField((Field) listener, pojo, wantedType);
		} else if (listener instanceof Method) {
			result = this.constructListenerFromMethod((Method) listener, pojo, wantedType);
		} else {
			throw new FiberException("Cannot create listener from " + listener + ": must be a field or method");
		}

		// note: we assume that a value coming from the IR is valid
		return result;
	}

	private <T, P, A> BiConsumer<T, T> constructListenerFromMethod(Method method, P pojo, Class<A> wantedType) throws FiberException {
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
		if (!method.getReturnType().equals(void.class)) throw new FiberException("Listener method must return void");
		int paramCount = method.getParameterCount();

		if ((paramCount != 1 && paramCount != 2) || !method.getParameterTypes()[0].equals(wantedType)) {
			throw new FiberException("Listener method must have exactly two parameters of type that it listens for");
		}

		return paramCount;
	}

	private <T, P, A> BiConsumer<T, T> constructListenerFromField(Field field, P pojo, Class<A> wantedType) throws FiberException {
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

	private static String findComment(Field field) {
		return getSettingAnnotation(field).map(Setting::comment).filter(s -> !s.isEmpty()).orElse(null);
	}

	private static String findName(Field field, SettingNamingConvention convention) {
		return Optional.ofNullable(
				field.isAnnotationPresent(Setting.Group.class)
						? field.getAnnotation(Setting.Group.class).name()
						: getSettingAnnotation(field).map(Setting::name).orElse(null))
				.filter(s -> !s.isEmpty())
				.orElse(convention.name(field.getName()));
	}

	private static SettingNamingConvention createConvention(Class<? extends SettingNamingConvention> namingConvention) throws FiberException {
		try {
			return namingConvention.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new FiberException("Could not initialise naming convention", e);
		}
	}
}
