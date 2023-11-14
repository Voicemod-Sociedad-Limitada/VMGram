package org.telegram.vm.adapters.models;

import java.util.Objects;

/**
 * This model is a representation of a Voicemod Voice but it is for UI purposes.
 */
public class UIVoice extends VoiceAdapterDataItem {
    public String id;
    public String name;
    private final String iconResourceKey;

    public UIVoice(String id, String name, String iconResourceKey) {
        this.id = id;
        this.name = name;
        this.iconResourceKey = iconResourceKey;
    }

    public String getIconResourceKey(){
        return iconResourceKey.replace("-","_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIVoice vmVoice = (UIVoice) o;
        return id == vmVoice.id && iconResourceKey == vmVoice.iconResourceKey && Objects.equals(name, vmVoice.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, iconResourceKey);
    }
}
