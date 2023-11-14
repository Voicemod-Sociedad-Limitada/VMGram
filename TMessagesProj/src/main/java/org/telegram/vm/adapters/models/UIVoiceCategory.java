package org.telegram.vm.adapters.models;

import java.util.Objects;

public class UIVoiceCategory extends VoiceAdapterDataItem {

    public String headerTextKey;

    public UIVoiceCategory(String name) {
        this.headerTextKey = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIVoiceCategory that = (UIVoiceCategory) o;
        return Objects.equals(headerTextKey, that.headerTextKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerTextKey);
    }
}
