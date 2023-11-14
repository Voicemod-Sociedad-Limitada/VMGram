package org.telegram.vm;

import android.content.Context;

import org.telegram.vm.adapters.VoiceApplyClickListener;
import org.telegram.vm.bottomsheet.BottomSheetUtils;
import org.telegram.vm.models.Voice;
import org.telegram.vm.models.VoiceCategory;
import org.telegram.vm.remoteconfig.firebase.FirebaseRemoteConfigRepository;
import org.telegram.vm.tracking.EntryPoint;

import java.util.function.Function;

public class VMChatActivityHelper {

    public boolean bottomSheetOpened;

    public void openBottomSheetDialog(Context context, VoiceApplyClickListener voiceApplyClickListener, EntryPoint entryPoint) {
        if (!bottomSheetOpened) {
            bottomSheetOpened = true;
            VoiceCategory[] categories = FirebaseRemoteConfigRepository.getInstance().getVoicesCategories();
            Voice[] voices = FirebaseRemoteConfigRepository.getInstance().getVoices();

            BottomSheetUtils.showVoicesBottomSheet(
                context,
                categories,
                voices,
                voiceApplyClickListener,
                voice -> {
                    VMUSDKStatsHelper.onVoicePreviewed(entryPoint, voice.name);
                },
                (nothing) -> {
                    bottomSheetOpened = false;
                    return null;
                }
            );
        }
    }
}