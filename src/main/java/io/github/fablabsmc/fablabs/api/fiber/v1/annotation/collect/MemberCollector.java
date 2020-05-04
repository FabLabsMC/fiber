package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect;

import java.lang.annotation.ElementType;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;

public interface MemberCollector {
	/**
	 * Tries to find all listeners in a specified POJO, passing
	 * them on to the given {@code processor}.
	 *
	 * <p>This method performs all logic to exclude members
	 * based on their properties, such as annotations or modifiers.
	 *
	 * <p>This method targets {@linkplain ElementType#FIELD} and {@linkplain ElementType#METHOD}.
	 *
	 * @param pojo      the instance of the POJO to scan
	 * @param clazz     the class to scan
	 * @param processor the member processor
	 * @param <P>       the generic pojo type
	 * @throws ProcessingMemberException if something went wrong while processing a member
	 */
	<P> void collectListeners(P pojo, Class<P> clazz, ListenerProcessor processor) throws ProcessingMemberException;

	/**
	 * Tries to find all settings and groups in a specified POJO,
	 * passing them on to the given {@code processor}.
	 *
	 * <p>This method performs all logic to exclude members
	 * based on their properties, such as annotations or modifiers.
	 *
	 * <p>This method targets {@linkplain ElementType#FIELD}.
	 *
	 * @param pojo      the instance of the POJO to scan
	 * @param clazz     the class to scan
	 * @param processor the member processor
	 * @param <P>       the generic pojo type
	 * @throws ProcessingMemberException if something went wrong while processing a member
	 */
	<P> void collectSettings(P pojo, Class<P> clazz, SettingProcessor processor) throws ProcessingMemberException;
}
