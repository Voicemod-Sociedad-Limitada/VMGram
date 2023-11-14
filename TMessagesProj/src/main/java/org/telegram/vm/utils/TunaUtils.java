package org.telegram.vm.utils;

import android.net.Uri;
import android.util.Pair;

import org.telegram.messenger.FileLoader;
import org.telegram.vm.adapters.models.UISound;
import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.sounds.utils.DownloaderHelper;
import org.telegram.vm.utils.download.DownloadResult;

import java.io.File;
import java.util.function.Function;

public class TunaUtils {

    public static DownloaderHelper downloaderHelper = new DownloaderHelper(true, "vmCachedSounds");

    public static void downloadTunaSoundAsOgg(UISound sound, Function<File,Void> onSuccess, Function<String,Void> onError){
        Uri destination = Uri.parse("file://" + FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE)+
                "/vmCachedSounds/"+sound.id+"_"+System.currentTimeMillis()+".ogg");

        if (sound.soundPathOgg != null){
            downloaderHelper.downloadToCacheFolder(sound.soundPathOgg,  (file) -> {
                SoundUtils.convertToOgg(
                        file.getPath(),
                        destination.getPath(),
                        onSuccess,
                        onError);
                return null;
            }, (progress) -> {
                return null;
            }, (error) ->{
                onError.apply(error);
                return null;
            });
        }else{
            downloaderHelper.downloadToCacheFolder(sound.soundPathMp3,  (file) -> {
                onSuccess.apply(file);
                return null;
            }, (progress) -> {
                return null;
            }, (error) ->{
                onError.apply(error);
                return null;
            });
        }
    }

    public static void downloadTunaSoundAsOgg(int operationId, UISound sound, Function<DownloadResult,Void> onSuccess, Function<String,Void> onError){
        Uri destination = Uri.parse("file://" + FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE)+
                "/vmCachedSounds/"+sound.id+"_"+System.currentTimeMillis()+".ogg");

        if (sound.soundPathOgg != null){
            downloaderHelper.downloadToCacheFolder(sound.soundPathOgg,  (file) -> {
                onSuccess.apply(new DownloadResult(operationId, file));
                return null;
            }, (progress) -> {
                return null;
            }, (error) ->{
                onError.apply(error);
                return null;
            });
        }else{
            downloaderHelper.downloadToCacheFolder(sound.soundPathMp3,  (file) -> {
                SoundUtils.convertToOgg(
                        file.getPath(),
                        destination.getPath(),
                        (File processedFile) -> {
                            onSuccess.apply(new DownloadResult(operationId, processedFile));
                            return null;
                        },
                        onError);
                return null;
            }, (progress) -> {
                return null;
            }, (error) ->{
                onError.apply(error);
                return null;
            });
        }
    }

}
