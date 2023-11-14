package org.telegram.vm.remoteconfig.models;

public class RemoteVoiceCategory {

    public String categoryKey;
    public String color;
    public String icon;
    public String[] voices;

    public RemoteVoiceCategory(String categoryKey, String color, String icon, String[] voices) {
        this.categoryKey = categoryKey;
        this.color = color;
        this.icon = icon;
        this.voices = voices;
    }

}
