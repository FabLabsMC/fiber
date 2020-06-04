package io.github.fablabsmc.fablabs.api.fiber.v1.annotation;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.BranchAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.LeafAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ParameterizedTypeProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.AnnotatedSettingsBuilderImpl;

/**
 * Types which implement this interface can create a config tree based on an
 * annotated POJO "plain old Java object" representation.
 */
public interface AnnotatedSettings {
	/**
	 * A provided {@link AnnotatedSettings} with configuration suitable
	 * for deserializing types supported in Fiber.
	 *
	 * @see #builder()
	 */
	AnnotatedSettings DEFAULT_SETTINGS = AnnotatedSettings.builder().build();

	/**
	 * Returns a annotated settings builder.
	 *
	 * @see Builder
	 */
	static AnnotatedSettings.Builder builder() {
		return new AnnotatedSettingsBuilderImpl();
	}

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
	 * @param pojo    The config schema.
	 * @param <P>     The type of the pojo.
	 * @throws FiberException If pojo cannot be parsed.
	 */
	<P> void applyToNode(ConfigTree mergeTo, P pojo) throws FiberException;

	interface Builder {
		AnnotatedSettings.Builder apply(Consumer<AnnotatedSettings.Builder> configuration);

		AnnotatedSettings.Builder useNamingConvention(SettingNamingConvention convention);

		AnnotatedSettings.Builder collectMembersRecursively();

		/**
		 * Specifies whether or not all fields in processed classes should be considered
		 * as config value candidates, or only those annotated with {@link Setting}.
		 *
		 * <p>This setting has no effect on classes annotated with {@link Settings @Settings},
		 * as it is superseded by {@link Settings#onlyAnnotated()}.
		 *
		 * @see Settings#onlyAnnotated()
		 */
		AnnotatedSettings.Builder collectOnlyAnnotatedMembers();

		AnnotatedSettings.Builder collectMembersWith(MemberCollector collector);

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
		 * @param type  The mapped to ConfigType.
		 * @param <T>   The type of fields to map.
		 * @return This instance.
		 * @see #registerTypeMapping(Class, ParameterizedTypeProcessor)
		 * @see ConfigType
		 */
		<T> AnnotatedSettings.Builder registerTypeMapping(Class<? super T> clazz, ConfigType<T, ?, ?> type);

		/**
		 * Registers a <em>type mapping</em> from a parameterized Java type to a family of {@link ConfigType}.
		 * Fields of instantiations of the given type are mapped to nodes storing values of
		 * {@link ConfigType} created via the given {@link ParameterizedTypeProcessor}.
		 *
		 * <p>This method should be used to register type mappings for parameterized types.
		 * For registering type mappings for non-parameterized types, use
		 * {@link #registerTypeMapping(Class, ConfigType)} instead.
		 *
		 * @param clazz     The Class object for the type of fields to map.
		 * @param processor A function taking actual type arguments and returning a suitable
		 *                  ConfigType.
		 * @param <T>       The type of fields to map.
		 * @return This instance.
		 * @see #registerTypeMapping(Class, ConfigType)
		 * @see ParameterizedTypeProcessor
		 */
		<T> AnnotatedSettings.Builder registerTypeMapping(Class<? super T> clazz, ParameterizedTypeProcessor<T> processor);

		/**
		 * Registers a setting annotation processor, tasked with processing annotations on config fields.
		 *
		 * @param <A>            the type of annotation to process
		 * @param annotationType a class representing the type of annotation to process
		 * @param processor      a processor for this annotation
		 * @return {@code this}, for chaining
		 * @throws IllegalArgumentException if {@code annotationType} does not have RUNTIME retention
		 */
		<A extends Annotation> AnnotatedSettings.Builder registerSettingProcessor(Class<A> annotationType, LeafAnnotationProcessor<A> processor);

		/**
		 * Registers a group annotation processor, tasked with processing annotations on ancestor fields (config fields annotated with {@link Setting.Group}.
		 *
		 * @param <A>            the type of annotation to process
		 * @param annotationType a class representing the type of annotation to process
		 * @param processor      a processor for this annotation
		 * @return {@code this}, for chaining
		 * @throws IllegalArgumentException if {@code annotationType} does not have RUNTIME retention
		 */
		<A extends Annotation> AnnotatedSettings.Builder registerGroupProcessor(Class<A> annotationType, BranchAnnotationProcessor<A> processor);

		/**
		 * Registers a constraint annotation processor, tasked with processing annotations on config types.
		 *
		 * @param annotationType a class representing the type of annotation to process
		 * @param processor      a processor for this annotation
		 * @param <A>            the type of annotation to process
		 * @return {@code this}, for chaining
		 * @throws IllegalArgumentException if {@code annotationType} does not have RUNTIME retention
		 */
		<A extends Annotation> AnnotatedSettings.Builder registerConstraintProcessor(Class<A> annotationType, ConstraintAnnotationProcessor<A> processor);

		AnnotatedSettings build();
	}
}
