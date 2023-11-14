package org.telegram.vm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import net.voicemod.vmgramservice.VMGramClient;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.vm.dto.VMVoice;
import org.telegram.vm.tracking.EntryPoint;
import org.telegram.vm.tracking.MessageType;
import org.telegram.vm.tracking.TrackingEvent;
import org.telegram.vm.tracking.TrackingHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.annotation.Nullable;

public class VMUSDKStatsHelper {

    private static final String tag = "VMUSDKStatsHelper";
    private static final String menuKey = "menu";
    private static final String soundNameKey = "sound_name";
    private static final String voiceNameKey = "voice_name";
    private static final String messageTypeKey = "message_type";
    private static final String voiceAvatarAppliedKey = "voice_avatar_applied";
    private static final String isCoreSoundKey = "is_core_sound";
    private static final String voicesUsedKey = "voices_used";
    private static final String coreSoundsUsedKey = "core_sounds_used";
    private static final String customSoundsUsedKey = "custom_sounds_used";
    private static final String usageTimeKey = "usage_time";
    private static final String voiceChangerActiveKey = "voice_changer_active";
    private static final String voiceChangerActiveTimeKey = "voice_changer_active_time";
    private static final String isVideoCallKey = "is_video_call";

    public static void getSendResetStats() {
        final VMVoice currentVoice = VMGramLiveData.getCallCurrentVoice();
        final boolean convEnabled = VMGramLiveData.getCallConversionEnabled();
        if (currentVoice != null && convEnabled) {
            //VMUSDKStatsHelperBroken - TODO: On USDKManager this method get stats from native code,
            //TODO: all this values are currently broken due VMGramService doesnt have the concept
            //TODO: of VoicemodUSDKStats. To fix port related code to TMessagesProj.
            //VMUSDKStatsHelperBroken - VoicemodUSDKStats stats = VoicemodUSDKManager.getStats();
            //VMUSDKStatsHelperBroken - VMUSDKStatsHelper.sendStats(stats);
        }
        //VMUSDKStatsHelperBroken - VoicemodUSDKManager.resetStats(); TODO: Whatever do resetStats also is broken
    }

    public static void sendStats(VoicemodUSDKStats stats) {
        if (stats == null) return;

        Map<String, Object> properties = new HashMap<>();
        properties.put("platform", "android");

        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            properties.put("vmgramVersionName", pInfo.versionName);
            properties.put("vmgramVersionCode", String.valueOf(pInfo.versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        properties.put("telegramVersionName", BuildVars.BUILD_VERSION_STRING);
        properties.put("telegramVersionCode", String.valueOf(BuildVars.BUILD_VERSION));
        properties.put("usdkVersion", VMGramClient.getVersionStringServiceOrCrash("VMUSDKStatsHelper::sendStats"));
        properties.put("voice", VMGramLiveData.getCallCurrentVoice().id);
        properties.put("audioCallbackDuration", String.valueOf(stats.audioCallbackDuration));
        properties.put("currentProcessBufferDuration", String.valueOf(stats.currentProcessBufferDuration));
        properties.put("aiNodeAverageCallDuration", String.valueOf(stats.aiNodeAverageCallDuration));
        properties.put("aiNodeCalls", String.valueOf(stats.aiNodeCalls));
        properties.put("aiNodeDropouts", String.valueOf(stats.aiNodeDropouts));
        properties.put("sampleRate", String.valueOf(VMGramLiveData.getVoIPSampleRate()));

        String systemFramesPerBuffer = getSystemFramesPerBuffer();
        properties.put("systemFramesPerBuffer", systemFramesPerBuffer != null ? systemFramesPerBuffer : "null");
        properties.put("framesPerBuffer", String.valueOf(stats.framesPerBuffer));

        properties.put("deviceModel", Build.MODEL);
        properties.put("deviceID", Build.ID);
        properties.put("deviceManufacturer", Build.MANUFACTURER);
        properties.put("deviceBrand", Build.BRAND);
        properties.put("deviceSDK", String.valueOf(Build.VERSION.SDK_INT));
        properties.put("deviceBoard", Build.BOARD);
        properties.put("deviceFingerprint", Build.FINGERPRINT);
        properties.put("deviceVersionCode", Build.VERSION.RELEASE);

        Log.d(tag, "Stats:");
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            Log.d(tag, entry.getKey() + " = " + entry.getValue());
        }

        TrackingEvent event = new TrackingEvent("VMGram call uSDK performance", properties);
        TrackingHelper.trackEvent(event);
    }

    /*
     * VMGram - Session Start: When app started to be used
     * */
    public static void onAppStarted() {
        TrackingEvent event = new TrackingEvent("VMGram - Session Start");
        TrackingHelper.trackEvent(event);
    }

    /*
     * VMGram - Session End: When app closes
     * */
    public static void onAppDestroyed() {
        TrackingEvent event = new TrackingEvent("VMGram - Session End");
        TrackingHelper.trackEvent(event);
    }

    /*
     * VMGram - Application Background: When app goes to background
     * */
    public static void onAppBackground() {
        TrackingEvent event = new TrackingEvent("VMGram - Application Background");
        TrackingHelper.trackEvent(event);
    }

    /*
     * VMGram - Application Foreground: When app opens foreground
     * */
    public static void onAppForeground() {
        TrackingEvent event = new TrackingEvent("VMGram - Application Foreground");
        TrackingHelper.trackEvent(event);
    }

    private static final LinkedHashSet<String> callVoicesUsed = new LinkedHashSet<>();
    private static final LinkedHashSet<String> customSoundUsed = new LinkedHashSet<>();

    /*
     * VMGram - Call Ended: A call or video call ends.
     * */
    public static void onCallEnded(
            double usageTime,
            boolean isVideoCall) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(voicesUsedKey, callVoicesUsed);
        properties.put(customSoundsUsedKey, customSoundUsed);
        properties.put(usageTimeKey, round(usageTime/1000,2));
        properties.put(voiceChangerActiveKey, !callVoicesUsed.isEmpty());
        properties.put(isVideoCallKey, isVideoCall);
        TrackingEvent event = new TrackingEvent("VMGram - Call Ended", properties);
        TrackingHelper.trackEvent(event);

        callVoicesUsed.clear();
        customSoundUsed.clear();
    }

    public static void onCallVoiceChanged(String voiceName){
        callVoicesUsed.add(voiceName);
    }

    public static void onCallSoundSend(String soundName){
        customSoundUsed.add(soundName);
    }

    public static double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /*
     * VMGram - Message Sent: A user sends an audio message.
     * */
    public static void onMessageSend(MessageType messageType, @Nullable String voiceOrSoundName, Boolean isCoreSound) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(messageTypeKey, messageType.title);
        if (messageType == MessageType.SOUND) {
            properties.put(soundNameKey, voiceOrSoundName);
            properties.put(isCoreSoundKey, isCoreSound);
        } else {
            properties.put(voiceAvatarAppliedKey, voiceOrSoundName);
        }
        TrackingEvent event = new TrackingEvent("VMGram - Message Sent", properties);
        TrackingHelper.trackEvent(event);
    }

    /*
     * VMGram - Voice Previewed: A voice is previewed. (The preview button for a voice is clicked)
     * */
    public static void onVoicePreviewed(EntryPoint entryPoint, String voiceName) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(menuKey, entryPoint.title);
        properties.put(voiceNameKey, voiceName);
        TrackingEvent event = new TrackingEvent("VMGram - Voice Previewed", properties);
        TrackingHelper.trackEvent(event);
    }

    /*
     * VMGram - Sound Previewed: A sound is previewed.
     * */
    public static void onSoundPreviewed(EntryPoint entryPoint, String soundName) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(menuKey, entryPoint.title);
        properties.put(soundNameKey, soundName);
        TrackingEvent event = new TrackingEvent("VMGram - Sound Previewed", properties);
        TrackingHelper.trackEvent(event);
    }

    private static String getSystemFramesPerBuffer() {
        final AudioManager audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
    }
}
