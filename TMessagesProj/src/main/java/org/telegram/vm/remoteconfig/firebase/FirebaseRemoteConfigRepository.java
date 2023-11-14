package org.telegram.vm.remoteconfig.firebase;

import android.util.Log;

import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.remoteconfig.IRemoteConfigRepository;
import org.telegram.vm.models.Voice;
import org.telegram.vm.models.VoiceCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class FirebaseRemoteConfigRepository implements IRemoteConfigRepository {

    static final String TAG = "FirebaseRemoteConfig";

    //Singleton
    private static FirebaseRemoteConfigRepository instance;
    public static FirebaseRemoteConfigRepository getInstance() {
        if (instance == null) {
            Log.e(TAG,"FirebaseRemoteConfigRepository not initialized, you must call init before getting an instance");
        }
        return instance;
    }

    public static void init(Function<Boolean, Void> initialized){
        instance = new FirebaseRemoteConfigRepository(initialized);
    }

    FirebaseRemoteConfigDataSource firebaseDataSource;

    public FirebaseRemoteConfigRepository(Function<Boolean, Void> initCallback) {
        firebaseDataSource = new FirebaseRemoteConfigDataSource((initializationResult) -> {
            initCallback.apply(initializationResult);
            return null;
        });
    }

    @Override
    public Voice[] getVoices() {
         return Arrays.stream(firebaseDataSource.getRemoteVoices().voicesFree).filter(voice -> voice.enabled).toArray(Voice[]::new);
    }

    @Override
    public VoiceCategory[] getVoicesCategories() {
        return Arrays.stream(firebaseDataSource.getRemoteVoiceCategories()).map(remoteVoiceCategory -> new VoiceCategory(
                remoteVoiceCategory.categoryKey,
                remoteVoiceCategory.color,
                remoteVoiceCategory.icon,
                getCategoryVoices(remoteVoiceCategory.voices))).toArray(VoiceCategory[]::new);
    }

    @Override
    public Sound[] getVoicemodeSelectionSounds() {
        return firebaseDataSource.getVoicemodSelectionSounds();
    }

    private Voice[] getCategoryVoices(String[] categoryVoicesIds){
        Voice[] voices = firebaseDataSource.getRemoteVoices().voicesFree;
        List<Voice> processedVoices = new LinkedList<>();

        for (int i=0;i<categoryVoicesIds.length;i++){
            String voiceId = categoryVoicesIds[i];
            Voice voice = Arrays.stream(voices).filter(voice1 -> voice1.id.equals(voiceId) && voice1.enabled).findFirst().orElse(null);
            if (voice != null) processedVoices.add(voice);
        }

        return processedVoices.toArray(new Voice[processedVoices.size()]);
    }
}
