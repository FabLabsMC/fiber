package me.zeroeightsix.fiber;

public class Identifier {
    String domain;
    String name;

    public Identifier(String domain, String name) {
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
}
