package org.telegram.vm.utils;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import net.voicemod.vmgramservice.VMGramClient;

import org.telegram.messenger.FileLoader;
import org.telegram.vm.adapters.models.UISound;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class SoundUtils {

    public static final String SOUND_PREVIEW_CANCELLED_ACTION = "VOICEMOD_SOUND_PREVIEW_CANCELLED";

    public static void convertAndPlayPreviewSound(
            String voiceId,
            Runnable onConversionStarted,
            Runnable onConversionFinished
    ) {

        onConversionStarted.run();
        VMGramClient vmGramClientInstance = VMGramClient.getInstance();

        vmGramClientInstance.cancelConvertAndPlay().thenAccept(pair -> {
            final String errorSource = "SoundUtils::convertAndPlayPreviewSound";
            VMGramClient.enableOfflineModeServiceOrCrash(true, errorSource);
            VMGramClient.setBypassServiceOrCrash(false, errorSource);
            VMGramClient.setSampleRateServiceOrCrash(44100.0f, errorSource);
            VMGramClient.loadVoiceServiceOrCrash(voiceId, errorSource);
            VMGramClient.enableBackgroundSoundsServiceOrCrash(true, errorSource);
            vmGramClientInstance.convertAndPlay(getRandomPreviewSoundSourceString()).thenAccept(p -> {
                onConversionFinished.run();
            });
        });
    }

    private static String getRandomPreviewSoundSourceString(){
        int index = ThreadLocalRandom.current().nextInt(1, 9);
        return"/android_asset/previewSounds/vm_voice_preview_0"+index+".wav";
    }

    public static void convertToOgg(Uri originUri, Uri destinationUri, Function<File,Void> onSuccess, Function<String,Void> onError) {
        convertToOgg(originUri.getPath(), destinationUri.getPath(), onSuccess, onError);
    }

    public static void convertToOgg(String originPath, String destinationPath, Function<File,Void> onSuccess, Function<String,Void> onError) {
        long executionId = FFmpeg.executeAsync("-i "+originPath+" -c:a libopus -b:a 64k "+destinationPath, (executionId1, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "Async command execution completed successfully.");
                onSuccess.apply(new File(destinationPath));
            } else if (returnCode == RETURN_CODE_CANCEL) {
                Log.i(Config.TAG, "Async command execution cancelled by user.");
            } else {
                Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                onError.apply(String.format("Async command execution failed with returnCode=%d.", returnCode));
            }
        });
    }

    public static void convertToPCM(String originPath, String destinationPath, Function<File,Void> onSuccess, Function<String,Void> onError) {
        long executionId = FFmpeg.executeAsync("-y -i "+originPath+" -acodec pcm_s16le -f s16le -ac 1 -ar 16000 "+destinationPath, (executionId1, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "PCM Async command execution completed successfully.");
                onSuccess.apply(new File(destinationPath));
            } else if (returnCode == RETURN_CODE_CANCEL) {
                Log.i(Config.TAG, "PCM Async command execution cancelled by user.");
            } else {
                Log.i(Config.TAG, String.format("PCM Async command execution failed with returnCode=%d.", returnCode));
                onError.apply(String.format("PCM Async command execution failed with returnCode=%d.", returnCode));
            }
        });
    }

    public static void convertOggToWav(String originPath, String destinationPath, Function<File,Void> onSuccess, Function<String,Void> onError){
        long executionId = FFmpeg.executeAsync("-i "+originPath+" -vn "+destinationPath, (executionId1, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "WAV Async command execution completed successfully.");
                onSuccess.apply(new File(destinationPath));
            } else if (returnCode == RETURN_CODE_CANCEL) {
                Log.i(Config.TAG, "WAV Async command execution cancelled by user.");
            } else {
                Log.i(Config.TAG, String.format("WAV Async command execution failed with returnCode=%d.", returnCode));
                onError.apply(String.format("WAV Async command execution failed with returnCode=%d.", returnCode));
            }
        });
    }

    public static int getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr);
    }

    public static void sendTunaSoundInACall(File file, String soundId, Function onSuccess, Function<String,Void> onError, boolean stopAllSoundsFirst){
        SoundUtils.convertOggToWav(file.getPath(), FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE)
                + "/vmCachedSounds/" + soundId + "_"+System.currentTimeMillis()+".wav", (wavFile) -> {
            String filePath = wavFile.getPath();
            String fileName = wavFile.getName();

            filePath = filePath.replace("/"+fileName, "");

            if (stopAllSoundsFirst) VMGramClient.stopSoundsServiceOrCrash("SoundUtils::sendTunaSoundInACall");
            VMGramClient.setHearSoundboardSelfOrCrash(true,"SoundUtils::sendTunaSoundInACall");
            VMGramClient.playSoundOrCrash("test", fileName, filePath, false, false, false, 1f, true, false, "SoundUtils::sendTunaSoundInACall");
            onSuccess.apply(null);
            return null;
        }, (error) -> {
            onError.apply(error);
            return null;
        });
    }

}
