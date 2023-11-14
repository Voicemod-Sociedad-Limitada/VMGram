package org.telegram.vm.adapters.models;

import java.util.Objects;

public class UISoundCategory {

    public String name;

    public UISoundCategory(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UISoundCategory that = (UISoundCategory) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
