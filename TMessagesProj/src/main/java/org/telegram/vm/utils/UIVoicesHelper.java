package org.telegram.vm.utils;

import android.content.Context;

import org.telegram.messenger.R;
import org.telegram.vm.adapters.models.UIVoice;

public class UIVoicesHelper {

    public static int getVoiceResourceId(UIVoice voice, Context context) {
        String voiceIconIdentifier = "voiceicon_" + voice.getIconResourceKey();
        int voiceIconResourceID = context.getResources().getIdentifier(voiceIconIdentifier,
            "drawable", context.getPackageName());
        return voiceIconResourceID != 0 ? voiceIconResourceID : R.drawable.dino_pic;
    }
}
