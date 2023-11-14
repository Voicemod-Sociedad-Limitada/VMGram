// This is the source code of VMGramClient
// It is licensed under GNU GPL v. 2 or later.
// You should have received a copy of the license in this archive (see LICENSE).
// Copyright Voicemod S.L, 2023.

package net.voicemod.vmgramservice;

import net.voicemod.vmgramservice.VmgsResultAidl;
import net.voicemod.vmgramservice.ParcelableInteger;
import net.voicemod.vmgramservice.ParcelableBoolean;
import net.voicemod.vmgramservice.ParcelableString;

interface VMGramAidl {

    String getResultDescription(in VmgsResultAidl result);
    String getVersionString();

    VmgsResultAidl initSDK(in String clientKey);
    VmgsResultAidl getVoiceNames(out List<String> voiceNames);
    VmgsResultAidl setSampleRate(int sampleRate);

    VmgsResultAidl loadVoice(in String voice);

    VmgsResultAidl enableBackgroundSounds(boolean enable);

    VmgsResultAidl playSoundboardSound(String id, String file, String Path,
                                       boolean loop, boolean muteOtherSounds, boolean muteVoice,
                                       float volume, boolean sendToVac, boolean fade);
    VmgsResultAidl stopAllSoundboardSounds();

    VmgsResultAidl setHearSoundboardSelf(boolean enable);

    void processBufferNative(inout float[] monoBuffer, int nFrames);

    VmgsResultAidl convertAndPlay(String source);
    VmgsResultAidl cancelConvertAndPlay();

    VmgsResultAidl enableOfflineMode(boolean offlineMode);

    VmgsResultAidl setBypass(boolean enable);

}
