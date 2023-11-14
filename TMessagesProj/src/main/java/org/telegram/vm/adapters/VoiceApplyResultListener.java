package org.telegram.vm.adapters;

public interface VoiceApplyResultListener {
    void onResultApplying(boolean isApplied, boolean shouldDismissed);
}
