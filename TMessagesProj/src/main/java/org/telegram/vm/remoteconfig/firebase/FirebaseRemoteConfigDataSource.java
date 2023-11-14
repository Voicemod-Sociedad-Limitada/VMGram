package org.telegram.vm.remoteconfig.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.telegram.messenger.BuildConfig;
import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.remoteconfig.IRemoteConfigDataSource;
import org.telegram.vm.remoteconfig.models.RemoteVoiceCategories;
import org.telegram.vm.remoteconfig.models.RemoteVoiceCategory;
import org.telegram.vm.remoteconfig.models.RemoteVoices;
import org.telegram.vm.remoteconfig.models.VoicemodSelectionSounds;
import java.util.function.Function;

public class FirebaseRemoteConfigDataSource implements IRemoteConfigDataSource {

    final String VOICES_KEY = "voices";
    final String CATEGORIES_KEY = "voicesCategories";
    final String VOICEMOD_SELECTION_SOUNDS_KEY = "VoicemodSelectionSounds";

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    boolean remoteConfigInitialized = false;

    public FirebaseRemoteConfigDataSource() {
        initFirebase();

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        remoteConfigInitialized  = task.isSuccessful();
                    }
                });
    }

    public FirebaseRemoteConfigDataSource(Function<Boolean, Void> initCallback) {
        initFirebase();

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        remoteConfigInitialized  = task.isSuccessful();

                        initCallback.apply(remoteConfigInitialized);
                    }
                });
    }

    private void initFirebase(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : 3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
    }



        @Override
    public RemoteVoices getRemoteVoices() {
        String json = (remoteConfigInitialized) ? mFirebaseRemoteConfig.getString(VOICES_KEY) : FirebaseRemoteConfigDefaultValues.defaultVoices;
        return RemoteVoices.fromJson(json);
    }

    @Override
    public RemoteVoiceCategory[] getRemoteVoiceCategories() {
        String json = (remoteConfigInitialized) ? mFirebaseRemoteConfig.getString(CATEGORIES_KEY) : FirebaseRemoteConfigDefaultValues.defaultCategories;
        return RemoteVoiceCategories.fromJson(json).categories;
    }

    @Override
    public Sound[] getVoicemodSelectionSounds() {
        String json = (remoteConfigInitialized) ? mFirebaseRemoteConfig.getString(VOICEMOD_SELECTION_SOUNDS_KEY) : "";
        VoicemodSelectionSounds voicemodSelectionSounds = VoicemodSelectionSounds.fromJson(json);
        if (voicemodSelectionSounds != null) {
            return voicemodSelectionSounds.soundSelection;
        } else {
            return null;
        }
    }
}
