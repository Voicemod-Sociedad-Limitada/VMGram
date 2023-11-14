package org.telegram.vm.remoteconfig;

import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.remoteconfig.models.RemoteVoices;
import org.telegram.vm.remoteconfig.models.RemoteVoiceCategory;

public interface IRemoteConfigDataSource {

    RemoteVoices getRemoteVoices();
    RemoteVoiceCategory[] getRemoteVoiceCategories();

    Sound[] getVoicemodSelectionSounds();
}
