package org.telegram.vm.tracking;

public enum EntryPoint {
    INSIDE_CALL("inside-call"),
    AUDIO_MESSAGE("audio-message"),
    START_CALL("start-call");

    public final String title;

    EntryPoint(String title) {
        this.title = title;
    }
}
