package org.telegram.vm;

import android.app.Activity;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;


import net.voicemod.vmgramservice.VMGramAidl;
import net.voicemod.vmgramservice.VMGramClient;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.vm.adapters.VoiceApplyClickListener;
import org.telegram.vm.adapters.models.UISound;
import org.telegram.vm.adapters.models.UIVoice;
import org.telegram.vm.bottomsheet.BottomSheetUtils;
import org.telegram.vm.bottomsheet.VMBottomSheet;
import org.telegram.vm.dto.VMVoice;
import org.telegram.vm.models.Voice;
import org.telegram.vm.models.VoiceCategory;
import org.telegram.vm.popupmenu.PopupMenuUtils;
import org.telegram.vm.remoteconfig.firebase.FirebaseRemoteConfigRepository;
import org.telegram.vm.tracking.EntryPoint;
import org.telegram.vm.utils.AnimationUtils;
import org.telegram.vm.utils.ApplicationExecutors;
import org.telegram.vm.utils.DpUtil;
import org.telegram.vm.utils.SoundUtils;
import org.telegram.vm.utils.UIVoicesHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

public class VMVoIPFragmentHelper {

    private final Handler handler = new Handler();

    private View voiceAppliedView;
    Animation voiceAppliedBottomAnimation;
    boolean toastAnimationInProgress;
    private UIVoice currentSelectedVoice;
    private SoundsListHelper soundsListHelper;
    public VMBottomSheet soundBottomSheet;
    private boolean bottomSheetOpened;
    private MediaPlayer mediaPlayer;

    private Runnable topViewRunnable;

    public void showVoicesAvatarPopupMenu(Activity activity, VMVoIPToggleButton voiceIdentityButton, ViewGroup viewGroup, boolean isVoiceIdentityEnabled) {
        Context newContext = new ContextThemeWrapper(activity, R.style.PopUpMenuVoiceIdentity);
        //New view because the popup menu must show below the icon instead below the text.
        View viewInNewPosition = new View(activity);
        viewInNewPosition.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        viewInNewPosition.setBackgroundColor(Color.TRANSPARENT);
        int[] location = new int[2];
        voiceIdentityButton.getLocationOnScreen(location);

        viewInNewPosition.setX(location[0] - 20);
        viewInNewPosition.setY(location[1] + voiceIdentityButton.getHeight() - 25);
        viewGroup.addView(viewInNewPosition);
        PopupMenu popup = new PopupMenu(newContext, viewInNewPosition);
        Menu menu = popup.getMenu();

        setPopUpMenuVisibleIcons(popup);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.voice_filter_menu, menu);

        PopupMenuUtils.changeFontSizePopUpMenuItems(menu);

        if (isVoiceIdentityEnabled) {
            popup.getMenu().getItem(2).setVisible(false);
        } else {
            popup.getMenu().getItem(0).setVisible(false);
            popup.getMenu().getItem(1).setVisible(false);
        }

        popup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.item_change_voice) {
                ApplicationExecutors exec = new ApplicationExecutors();
                openBottomSheetDialog(activity, (voice, resultListener) -> {
                    VMUSDKStatsHelper.getSendResetStats();
                    exec.getBackground().execute(() -> {
                        VMGramClient.loadVoiceServiceOrCrash(voice.id, "VMVoIPFragmentHelper::showVoicesAvatarPopupMenu item_change_voice");
                        VMGramClient.setNativeVoice(voice.id);
                        VMGramLiveData.setCallCurrentVoice(new VMVoice(voice.id, voice.name));
                        VMGramLiveData.setCallConversionEnabled(true);
                        exec.getMainThread().execute(() -> {
                            resultListener.onResultApplying(true, true);
                        });
                    });
                });
            } else if (menuItem.getItemId() == R.id.item_voice_filter_off) {
                VMUSDKStatsHelper.getSendResetStats();

                VMGramLiveData.setCallConversionEnabled(false);
                VMGramClient.setBypassServiceOrCrash(true, "VMVoIPFragmentHelper::showVoicesAvatarPopupMenu item_voice_filter_off");
                VMGramClient.setNativeVoice(null);
                voiceAppliedView.setVisibility(View.INVISIBLE);
                if (topViewRunnable != null) {
                    handler.removeCallbacks(topViewRunnable);
                    handler.post(topViewRunnable);
                }
            } else if (menuItem.getItemId() == R.id.item_voice_filter_on) {
                turnOnVoiceAvatar(activity);

            } else if (menuItem.getItemId() == R.id.item_sound_effect) {
                if (soundsListHelper == null)
                    soundsListHelper = new SoundsListHelper(newContext, EntryPoint.INSIDE_CALL);
                View voiceApplied = viewGroup.findViewById(R.id.vm_voice_applied_toast);
                int bottom;
                if (VMGramLiveData.getCallConversionEnabled()) {
                    bottom = voiceApplied != null ? voiceApplied.getBottom() : AndroidUtilities.dp(220);
                } else {
                    bottom = AndroidUtilities.dp(170);
                }
                soundBottomSheet = showSoundsBottomSheet(newContext, soundsListHelper, bottom);
            }
            return false;
        });

        voiceIdentityButton.changeColors(ContextCompat.getColor(newContext, R.color.vm_white), false,
                ContextCompat.getColor(newContext, R.color.vm_black), true);

        popup.show();

        popup.setOnDismissListener(popupMenu -> {
            voiceIdentityButton.changeColors(ContextCompat.getColor(newContext, R.color.vm_white), true,
                    ContextCompat.getColor(newContext, R.color.vm_white), false);
        });
    }

    private VMBottomSheet showSoundsBottomSheet(Context context, SoundsListHelper soundsListHelper, int voiceAppliedBottom) {
        VMBottomSheet bottomSheet = BottomSheetUtils.showSoundsBottomSheetInCallScreen(context, soundsListHelper, (nothing) -> {
            VMGramClient.stopSoundsServiceOrCrash("VMVoIPFragmentHelper::showSoundsBottomSheet::lambda");
            if (mediaPlayer != null) mediaPlayer.stop();
            return null;
        });
        soundsListHelper.setOnSoundSelected(new SoundsListHelper.OnSoundListListener() {
            @Override
            public void soundSelected(File selectedFile, short[] soundBytes, UISound selectedSound) {
                bottomSheet.showSoundSentTopToast(selectedSound, voiceAppliedBottom);

                VMUSDKStatsHelper.onCallSoundSend(selectedSound.name);

                SoundUtils.sendTunaSoundInACall(
                        selectedFile,
                        selectedSound.id,
                        (nothing)->{ return null; },
                        (error)->{ return null;},
                        true
                );

                if (mediaPlayer == null) mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(selectedFile.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mp -> {
                        mediaPlayer.reset();
                        soundsListHelper.setAdapterSoundStoppedPlaying();
                    });
                    soundsListHelper.setAdapterCurrentPlayingAudioDuration(mediaPlayer.getDuration());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void soundDownloadCancelled(UISound selectedSound) {

            }

            @Override
            public void onCurrentSoundStopped() {
                VMGramClient.stopSoundsServiceOrCrash("VMVoIPFragmentHelper::showSoundsBottomSheet::onCurrentSoundStopped");
                if (mediaPlayer != null) mediaPlayer.stop();
            }

            @Override
            public void onScrollChangeListener(int scroll) {

            }

            @Override
            public void onHideSearchKeyboard() {

            }
        });

        return bottomSheet;
    }

    private void turnOnVoiceAvatar(Activity activity) {
        VMUSDKStatsHelper.getSendResetStats();

        VMVoice selectedVoice = VMGramLiveData.getCallCurrentVoice();
        if (selectedVoice == null) {
            ApplicationExecutors exec = new ApplicationExecutors();
            openBottomSheetDialog(activity, (voice, resultListener) -> {
                exec.getBackground().execute(() -> {
                    VMGramClient.loadVoiceServiceOrCrash(voice.id, "VMVoIPFragmentHelper::turnOnVoiceAvatar");
                    VMGramClient.setBypassServiceOrCrash(false, "VMVoIPFragmentHelper::turnOnVoiceAvatar selected voice == null");
                    VMGramClient.setNativeVoice(voice.id);
                    VMGramLiveData.setCallCurrentVoice(new VMVoice(voice.id, voice.name));
                    VMGramLiveData.setCallConversionEnabled(true);
                    exec.getMainThread().execute(() -> {
                        resultListener.onResultApplying(true, true);
                    });
                });
            });
        } else {
            VMGramClient.setBypassServiceOrCrash(false, "VMVoIPFragmentHelper::turnOnVoiceAvatar selected voice != null");
            VMGramClient.setNativeVoice(selectedVoice.id);
            VMGramLiveData.setCallConversionEnabled(true);
            setVoiceAppliedView(activity, voiceAppliedView, getSelectedUIVoice(selectedVoice.id));
        }
    }

    private void setPopUpMenuVisibleIcons(PopupMenu popup) {
        try {
            Method method = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(popup.getMenu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinearLayout createButtonLinearLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, 0, 0, 40);
        linearParams.gravity = Gravity.CENTER_HORIZONTAL;
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(linearParams);
        return linearLayout;
    }

    public LinearLayout createLinearLayoutButtons(Context context, VMVoIPToggleButton voiceIdentityButton, VMVoIPToggleButton[] bottomButtons) {
        LinearLayout linearLayoutButtons = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        linearLayoutButtons.setLayoutParams(params);
        linearLayoutButtons.setOrientation(LinearLayout.VERTICAL);

        LinearLayout voiceIdentityLinearLayout = createButtonLinearLayout(context);
        voiceIdentityLinearLayout.addView(voiceIdentityButton);

        linearLayoutButtons.addView(voiceIdentityLinearLayout);

        LinearLayout middleButtonsLinearLayout = createButtonLinearLayout(context);
        int margin = DpUtil.convertDpToPixel(15, context);
        for (int i = 0; i < 3; i++) {
            FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.setMargins(margin, 0, margin, 0);
            bottomButtons[i].setLayoutParams(buttonParams);
            middleButtonsLinearLayout.addView(bottomButtons[i]);
        }
        linearLayoutButtons.addView(middleButtonsLinearLayout);
        LinearLayout endCallLinearLayout = createButtonLinearLayout(context);
        endCallLinearLayout.addView(bottomButtons[3]);
        linearLayoutButtons.addView(endCallLinearLayout);
        return linearLayoutButtons;
    }

    void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) != -1) {
            target.write(buf, 0, length);
        }
    }

    public void setDataVoiceIdentityButton(boolean animated, VMVoIPToggleButton voiceIdentityButton) {
        int icon = R.drawable.ic_sound_effects;
        voiceIdentityButton.setData(icon,
                Color.WHITE, ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.12f)),
                LocaleController.getString("VoiceIdentityButton", R.string.VoiceFilterButton),
                false, animated);
    }

    public VMVoIPToggleButton createVoiceIdentityButton(Context context, Activity activity, ViewGroup fragmentView) {
        VMVoIPToggleButton voiceIdentityButton = new VMVoIPToggleButton(context);
        voiceIdentityButton.setOnClickListener(view -> {
            showVoicesAvatarPopupMenu(activity, (VMVoIPToggleButton) view, fragmentView,
                VMGramLiveData.getCallConversionEnabled()
            );
        });
        return voiceIdentityButton;
    }

    public UIVoice getSelectedUIVoice(String voiceId) {
        Voice[] selectedVoice = Arrays.stream(FirebaseRemoteConfigRepository.getInstance().getVoices())
                .filter(voice -> voice.id.equals(voiceId)).toArray(Voice[]::new);
        if (selectedVoice == null || selectedVoice.length == 0) {
            return null;
        }
        return new UIVoice(selectedVoice[0].id, selectedVoice[0].name, selectedVoice[0].id);
    }

    public void showVoiceAppliedTopToast(Context context, View voiceAppliedTopView, View voiceAppliedView,String voiceId) {
        if (voiceId == null || voiceId.isEmpty()) {
            return;
        }

        if (toastAnimationInProgress){
            currentSelectedVoice = getSelectedUIVoice(voiceId);
            configTopToast(voiceAppliedTopView, context, currentSelectedVoice);
            configBottomToast(voiceAppliedView, context, currentSelectedVoice);
            return;
        }

        toastAnimationInProgress = true;

        this.voiceAppliedView = voiceAppliedView;

        currentSelectedVoice = getSelectedUIVoice(voiceId);

        voiceAppliedView.setVisibility(View.INVISIBLE);

        configTopToast(voiceAppliedTopView,context, currentSelectedVoice);

        voiceAppliedView.getBackground().setColorFilter(new BlendModeColorFilter(ContextCompat.getColor(context, R.color.vm_white_50), BlendMode.SCREEN));
        AnimationUtils.fadeInAnimation(voiceAppliedTopView, 750);

        topViewRunnable = () -> {
            if (voiceAppliedTopView.getVisibility() == View.VISIBLE && voiceAppliedTopView.getAnimation() == null) {
                AnimationUtils.fadeOutAnimation(voiceAppliedTopView, 750);
            }
            if (VMGramLiveData.getCallConversionEnabled()) {
                setVoiceAppliedView(context, voiceAppliedView, currentSelectedVoice);
            }
        };

        handler.postDelayed(topViewRunnable, 3500);
    }

    private void configTopToast(View voiceAppliedTopView, Context context, UIVoice voice){
        ((TextView) voiceAppliedTopView.findViewById(R.id.toast_tv)).setText(LocaleController.formatString("VoiceApplied", R.string.VoiceApplied, voice.name));
        ((ImageView) voiceAppliedTopView.findViewById(R.id.toast_img))
                .setImageDrawable(ContextCompat.getDrawable(context, UIVoicesHelper.getVoiceResourceId(voice, context)));
    }

    private void configBottomToast(View voiceAppliedView, Context context, UIVoice voice){
        ((TextView) voiceAppliedView.findViewById(R.id.toast_tv)).setText(voice.name);
        ((ImageView) voiceAppliedView.findViewById(R.id.toast_img))
                .setImageDrawable(ContextCompat.getDrawable(context, UIVoicesHelper.getVoiceResourceId(voice, context)));
    }

    public void setVoiceAppliedView(Context context, View voiceAppliedView, UIVoice selectedVoice) {
        configBottomToast(voiceAppliedView,context,selectedVoice);

        voiceAppliedView.getBackground().setColorFilter(new BlendModeColorFilter(ContextCompat.getColor(context, R.color.vm_white_50), BlendMode.DST_IN));
        voiceAppliedView.setVisibility(View.VISIBLE);

        voiceAppliedBottomAnimation = AnimationUtils.fadeInAnimation(voiceAppliedView, 750);
        voiceAppliedBottomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toastAnimationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void openBottomSheetDialog(Context context, VoiceApplyClickListener voiceApplyClickListener) {
        if (!bottomSheetOpened) {
            bottomSheetOpened = true;
            VoiceCategory[] categories = FirebaseRemoteConfigRepository.getInstance().getVoicesCategories();
            Voice[] voices = FirebaseRemoteConfigRepository.getInstance().getVoices();

            BottomSheetUtils.showVoicesBottomSheetInCallScreen(
                context,
                categories,
                voices,
                voiceApplyClickListener,
                voice -> {
                    VMUSDKStatsHelper.onVoicePreviewed(EntryPoint.INSIDE_CALL, voice.name);
                },
                (nothing) -> {
                    bottomSheetOpened = false;
                    return null;
                }
            );
        }
    }
}