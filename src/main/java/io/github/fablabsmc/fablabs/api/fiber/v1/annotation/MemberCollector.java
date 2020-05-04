package io.github.fablabsmc.fablabs.api.fiber.v1.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.MemberCollectorImpl;

public interface MemberCollector<C> {
	/**
	 * Returns the set of fields that the annotation processor should process.
	 *
	 * <p>This method performs logic to exclude fields based on
	 * their properties, such as visibility or annotations.
	 *
	 * @param pojo    the POJO to collect fields from
	 * @param builder the builder being configured
	 * @return the collected set of fields
	 */
	Set<Field> collectFields(Object pojo, C builder);

	/**
	 * Returns the set of methods that the annotation processor should process.
	 *
	 * <p>This method performs logic to exclude methods based on
	 * their properties, such as visibility or annotations.
	 *
	 * @param pojo    the POJO to collect methods from
	 * @param builder the builder being configured
	 * @return the collected set of methods
	 */
	Set<Method> collectMethods(Object pojo, C builder);

	static MemberCollector<ConfigTreeBuilder> create() {
		return new MemberCollectorImpl();
	}
}
