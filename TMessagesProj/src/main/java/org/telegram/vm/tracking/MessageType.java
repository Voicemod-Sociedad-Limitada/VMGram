package org.telegram.vm.tracking;

public enum MessageType {
    VOICE("voice"),
    VIDEO("video"),
    SOUND("sound");

    public final String title;

    MessageType(String title) {
        this.title = title;
    }
}
