package org.telegram.vm.adapters;

import org.telegram.vm.adapters.models.UIVoice;

public interface VoiceApplyClickListener {

    void onApplyVoiceClick(UIVoice voice, VoiceApplyResultListener result);

}
