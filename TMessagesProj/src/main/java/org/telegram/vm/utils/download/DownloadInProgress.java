package org.telegram.vm.utils.download;

import org.telegram.vm.adapters.models.UISound;

public class DownloadInProgress{
    public int id;
    public UISound uiSound;
    public boolean cancelled;

    public DownloadInProgress(int id, UISound sound, boolean cancelled) {
        this.id = id;
        this.uiSound = sound;
        this.cancelled = cancelled;
    }
}