package org.telegram.vm.models;

public class Voice {

    public String id;
    public String name;
    public boolean enabled;
    public boolean hasBackgroundSound;

    public Voice(String id, String name, boolean enabled, boolean hasBackgroundSound) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.hasBackgroundSound = hasBackgroundSound;
    }

}
