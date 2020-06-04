package io.github.fablabsmc.fablabs.api.fiber.v1.exception;

import java.lang.reflect.Member;

/**
 * Thrown when an error occurs within annotated member processing.
 */
public class ProcessingMemberException extends FiberException {
	private final Member member;

	public ProcessingMemberException(String message, Member member) {
		super(message);
		this.member = member;
	}

	public ProcessingMemberException(String message, Throwable cause, Member member) {
		super(message, cause);
		this.member = member;
	}

	public Member getMember() {
		return this.member;
	}
}
