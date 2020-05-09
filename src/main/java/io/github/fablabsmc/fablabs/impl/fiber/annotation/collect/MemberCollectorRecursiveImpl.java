package io.github.fablabsmc.fablabs.impl.fiber.annotation.collect;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.PojoMemberProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;

public class MemberCollectorRecursiveImpl extends MemberCollectorImpl {
	@Override
	public <P> void collect(P pojo, Class<? super P> clazz, PojoMemberProcessor processor) throws ProcessingMemberException {
		super.collect(pojo, clazz, processor);

		if (clazz.getSuperclass() != null) {
			this.collect(pojo, clazz.getSuperclass(), processor);
		}
	}
}
