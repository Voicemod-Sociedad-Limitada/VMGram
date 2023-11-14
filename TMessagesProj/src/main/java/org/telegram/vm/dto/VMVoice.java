package org.telegram.vm.dto;

import java.util.Objects;

public class VMVoice {
    public String id;
    public String name;

    public VMVoice(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VMVoice vmVoice = (VMVoice) o;
        return id == vmVoice.id && Objects.equals(name, vmVoice.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
