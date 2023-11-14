package org.telegram.vm.remoteconfig;

import org.telegram.vm.models.Voice;
import org.telegram.vm.models.VoiceCategory;
import org.telegram.vm.models.tuna.Sound;

public interface IRemoteConfigRepository {

    Voice[] getVoices();
    VoiceCategory[] getVoicesCategories();

    Sound[] getVoicemodeSelectionSounds();
}
