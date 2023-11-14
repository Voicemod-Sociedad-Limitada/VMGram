package org.telegram.vm.models;

import java.util.Arrays;
import java.util.Objects;

public class VoiceCategory {

    public String categoryKey;
    public String color;
    public String icon;
    public Voice[] voices;

    public VoiceCategory(String categoryKey, String color, String icon, Voice[] voices) {
        this.categoryKey = categoryKey;
        this.color = color;
        this.icon = icon;
        this.voices = voices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceCategory that = (VoiceCategory) o;
        return Objects.equals(categoryKey, that.categoryKey) && Objects.equals(color, that.color) && Objects.equals(icon, that.icon) && Arrays.equals(voices, that.voices);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(categoryKey, color, icon);
        result = 31 * result + Arrays.hashCode(voices);
        return result;
    }
}
