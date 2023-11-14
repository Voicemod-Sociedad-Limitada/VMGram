package org.telegram.vm.bottomsheet;

import org.telegram.vm.models.Voice;
import org.telegram.vm.models.VoiceCategory;

public class BottomSheetConfig {

    public Voice[] voices;
    public VoiceCategory[] voicesCategories;
    public boolean previewVoiceVisible;
    public boolean showAppliedVoice;

    public BottomSheetConfig setVoices(Voice[] voices) {
        this.voices = voices;
        return this;
    }

    public BottomSheetConfig setVoicesCategories(VoiceCategory[] voicesCategories) {
        this.voicesCategories = voicesCategories;
        return this;
    }

    public BottomSheetConfig setPreviewVoiceVisible(boolean previewVoiceVisible) {
        this.previewVoiceVisible = previewVoiceVisible;
        return this;
    }

    public BottomSheetConfig setShowAppliedVoice(boolean showAppliedVoice) {
        this.showAppliedVoice = showAppliedVoice;
        return this;
    }
}
