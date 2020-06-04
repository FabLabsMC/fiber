package io.github.fablabsmc.fablabs.api.fiber.v1;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A namespaced string, representing an identifier without
 * character restrictions.
 *
 * <p>A {@code FiberId} contains two names: the domain and the name. The domain and name
 * may be represented as a single string by separating them with a colon ({@code :}).
 */
public final class FiberId {
	private final String domain;
	private final String name;

	public FiberId(@Nonnull String domain, @Nonnull String name) {
		this.domain = domain;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}

	@Override
	public String toString() {
		return getDomain() + ":" + getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FiberId fiberId = (FiberId) o;
		return domain.equals(fiberId.domain) && name.equals(fiberId.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(domain, name);
	}
}
