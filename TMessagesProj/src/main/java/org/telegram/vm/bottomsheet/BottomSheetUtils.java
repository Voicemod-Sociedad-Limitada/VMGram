package org.telegram.vm.bottomsheet;

import android.content.Context;

import org.telegram.vm.SoundsListHelper;
import org.telegram.vm.adapters.VoiceApplyClickListener;
import org.telegram.vm.adapters.VoicePreviewClickListener;
import org.telegram.vm.adapters.models.UISound;
import org.telegram.vm.models.Voice;
import org.telegram.vm.models.VoiceCategory;

import java.util.function.Function;


public class BottomSheetUtils {

    public static void showVoicesBottomSheet(Context context, VoiceCategory[] voicesCategories, Voice[] voices, VoiceApplyClickListener onApplyListener, VoicePreviewClickListener onPreviewListener, Function<Void,Void> onDismissed){
       BottomSheetBuilder builder = new BottomSheetBuilder();

        BottomSheetConfig config = new BottomSheetConfig();
        config.setVoices(voices)
                .setVoicesCategories(voicesCategories)
                .setPreviewVoiceVisible(true)
                .setShowAppliedVoice(false);

       builder.buildVoicesBottomSheet(context, config, onApplyListener, onPreviewListener, onDismissed).show();
    }

    public static void showVoicesBottomSheetInCallScreen(Context context, VoiceCategory[] voicesCategories, Voice[] voices, VoiceApplyClickListener onApplyListener, VoicePreviewClickListener onPreviewListener, Function<Void,Void> onDismissed){
        BottomSheetBuilder builder = new BottomSheetBuilder();

        BottomSheetConfig config = new BottomSheetConfig();
        config.setVoices(voices)
                .setVoicesCategories(voicesCategories)
                .setPreviewVoiceVisible(false)
                .setShowAppliedVoice(true);

        builder.buildVoicesBottomSheet(context, config, onApplyListener, onPreviewListener, onDismissed).show();
    }

    public static VMBottomSheet showSoundsBottomSheetInCallScreen(Context context, SoundsListHelper soundsListHelper, Function<Void,Void> onDismissed){
        BottomSheetBuilder builder = new BottomSheetBuilder();
        VMBottomSheet bottomSheet = builder.buildSoundsBottomSheet(context, soundsListHelper, onDismissed);
        bottomSheet.show();
        return bottomSheet;
    }

}
