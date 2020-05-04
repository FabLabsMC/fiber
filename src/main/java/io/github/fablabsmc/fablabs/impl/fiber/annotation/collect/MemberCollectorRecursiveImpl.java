package io.github.fablabsmc.fablabs.impl.fiber.annotation.collect;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.ListenerProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.SettingProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;

public class MemberCollectorRecursiveImpl extends MemberCollectorImpl {
	@Override
	public <P> void collectListeners(P pojo, Class<P> clazz, ListenerProcessor processor) {
		super.collectListeners(pojo, clazz, processor);

		if (clazz.getSuperclass() != null) {
			super.collectListeners(pojo, clazz.getSuperclass(), processor);
		}
	}

	@Override
	public <P> void collectSettings(P pojo, Class<P> clazz, SettingProcessor processor) throws ProcessingMemberException {
		super.collectSettings(pojo, clazz, processor);

		if (clazz.getSuperclass() != null) {
			super.collectSettings(pojo, clazz.getSuperclass(), processor);
		}
	}
}
