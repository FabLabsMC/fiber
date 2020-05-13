package io.github.fablabsmc.fablabs.api.fiber.v1.annotation;

import java.lang.annotation.Annotation;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.BranchAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.LeafAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ParameterizedTypeProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.AnnotatedSettingsImpl;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.collect.MemberCollectorImpl;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.collect.MemberCollectorRecursiveImpl;

/**
 * Types which implement this interface can create a config tree based on an
 *  annotated POJO "plain old Java object" representation.
 */
public interface AnnotatedSettings {
	/**
	 * A provided {@link AnnotatedSettings} with configuration suitable
	 * for deserializing types supported in Fiber.
	 *
	 * @see #create()
	 */
	AnnotatedSettings DEFAULT_SETTINGS = create();

	/**
	 * Creates a {@link AnnotatedSettings} with configuration suitable
	 * for deserializing types supported in Fiber.
	 */
	static AnnotatedSettings create() {
		return create(new MemberCollectorImpl());
	}

	static AnnotatedSettings createRecursive() {
		return create(new MemberCollectorRecursiveImpl());
	}

	static AnnotatedSettings create(MemberCollector collector) {
		return new AnnotatedSettingsImpl(collector);
	}

	/* tree building methods */

	/**
	 * Creates a config tree based on the given argument.
	 *
	 * @param pojo The config schema.
	 * @return A config tree defined by the representation.
	 * @throws FiberException If pojo cannot be parsed.
	 */
	ConfigBranch makeTree(Object pojo) throws FiberException;

	/**
	 * Applies the schema defined by {@code pojo} to the given {@link ConfigTree}.
	 *
	 * @param mergeTo The config tree.
	 * @param pojo The config schema.
	 * @param <P> The type of the pojo.
	 * @throws FiberException If pojo cannot be parsed.
	 */
	<P> void applyToNode(ConfigTree mergeTo, P pojo) throws FiberException;

	/* configuration methods */

	/**
	 * Registers a <em>type mapping</em> from a Java type to a {@link ConfigType}.
	 * Fields of the given type in POJOs are mapped to nodes storing values of the
	 * given {@link ConfigType}.
	 *
	 * <p>This method should be used to register type mappings for non-parameterized types.
	 * For registering type mappings for parameterized types, use
	 * {@link #registerTypeMapping(Class, ParameterizedTypeProcessor)} instead.
	 *
	 * @param clazz The Class object for the type of fields to map.
	 * @param type The mapped to ConfigType.
	 * @param <T> The type of fields to map.
	 * @return This instance.
	 * @see #registerTypeMapping(Class, ParameterizedTypeProcessor)
	 * @see ConfigType
	 */
	<T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, ConfigType<T, ?, ?> type);

	/**
	 * Registers a <em>type mapping</em> from a parameterized Java type to a family of {@link ConfigType}.
	 * Fields of instantiations of the given type are mapped to nodes storing values of
	 * {@link ConfigType} created via the given {@link ParameterizedTypeProcessor}.
	 *
	 * <p>This method should be used to register type mappings for parameterized types.
	 * For registering type mappings for non-parameterized types, use
	 * {@link #registerTypeMapping(Class, ConfigType)} instead.
	 *
	 * @param clazz The Class object for the type of fields to map.
	 * @param processor A function taking actual type arguments and returning a suitable
	 *                  ConfigType.
	 * @param <T> The type of fields to map.
	 * @return This instance.
	 * @see #registerTypeMapping(Class, ConfigType)
	 * @see ParameterizedTypeProcessor
	 */
	<T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, ParameterizedTypeProcessor<T> processor);

	/**
	 * Registers a setting annotation processor, tasked with processing annotations on config fields.
	 *
	 * @param <A>            the type of annotation to process
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @return {@code this}, for chaining
	 * @throws IllegalArgumentException if {@code annotationType} does not have RUNTIME retention
	 */
	<A extends Annotation> AnnotatedSettings registerSettingProcessor(Class<A> annotationType, LeafAnnotationProcessor<A> processor);

	/**
	 * Registers a group annotation processor, tasked with processing annotations on ancestor fields (config fields annotated with {@link Setting.Group}.
	 *
	 * @param <A>            the type of annotation to process
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @return {@code this}, for chaining
	 * @throws IllegalArgumentException if {@code annotationType} does not have RUNTIME retention
	 */
	<A extends Annotation> AnnotatedSettings registerGroupProcessor(Class<A> annotationType, BranchAnnotationProcessor<A> processor);

	/**
	 * Registers a constraint annotation processor, tasked with processing annotations on config types.
	 *
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @param <A>            the type of annotation to process
	 * @return {@code this}, for chaining
	 * @throws IllegalArgumentException if {@code annotationType} does not have RUNTIME retention
	 */
	<A extends Annotation> AnnotatedSettings registerConstraintProcessor(Class<A> annotationType, ConstraintAnnotationProcessor<A> processor);
}
