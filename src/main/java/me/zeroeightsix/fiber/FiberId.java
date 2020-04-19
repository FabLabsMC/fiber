package me.zeroeightsix.fiber;

import javax.annotation.Nonnull;
import java.util.Objects;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiberId fiberId = (FiberId) o;
        return domain.equals(fiberId.domain) &&
                name.equals(fiberId.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, name);
    }
}