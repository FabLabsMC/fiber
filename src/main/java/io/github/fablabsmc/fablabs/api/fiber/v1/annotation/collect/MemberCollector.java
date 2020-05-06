package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect;

import java.lang.annotation.ElementType;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;

public interface MemberCollector {
	/**
	 * Tries to find all listeners, settings, and groups in a specified POJO,
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
	<P> void collect(P pojo, Class<? super P> clazz, SettingProcessor processor) throws ProcessingMemberException;
}
