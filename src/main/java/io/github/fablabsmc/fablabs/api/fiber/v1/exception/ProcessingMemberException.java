package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

import java.lang.reflect.Member;

public class ProcessingMemberException extends FiberException {
	final Member member;

	public ProcessingMemberException(String message, Throwable cause, Member member) {
		super(message, cause);
		this.member = member;
	}
}
