package org.telegram.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.telegram.vm.dto.VMVoice;

public class VMGramLiveData {

    private static final MutableLiveData<Boolean> _callConversionEnabled = new MutableLiveData<>(false);
    public static final LiveData<Boolean> callConversionEnabled = _callConversionEnabled;
    public static void setCallConversionEnabled(boolean enabled) {
        _callConversionEnabled.postValue(enabled);
    }
    public static boolean getCallConversionEnabled() {
        return Boolean.TRUE.equals(_callConversionEnabled.getValue());
    }

    private static final MutableLiveData<Boolean> _recordingConversionEnabled = new MutableLiveData<>(false);
    public static final LiveData<Boolean> recordingConversionEnabled = _recordingConversionEnabled;
    public static void setRecordingConversionEnabled(boolean enabled) {
        _recordingConversionEnabled.postValue(enabled);
    }
    public static boolean getRecordingConversionEnabled() {
        return Boolean.TRUE.equals(_callConversionEnabled.getValue());
    }

    private static final MutableLiveData<VMVoice> _callSelectedVoice = new MutableLiveData<>(null);
    public static final LiveData<VMVoice> callSelectedVoice = _callSelectedVoice;
    public static void setCallCurrentVoice(VMVoice voice){
        _callSelectedVoice.postValue(voice);
    }
    public static VMVoice getCallCurrentVoice() {
        return _callSelectedVoice.getValue();
    }

    private static final MutableLiveData<VMVoice> _recordingSelectedVoice = new MutableLiveData<>(null);
    public static final LiveData<VMVoice> recordingSelectedVoice = _recordingSelectedVoice;
    public static void setRecordingCurrentVoice(VMVoice voice){
        _recordingSelectedVoice.postValue(voice);
    }
    public static VMVoice getRecordingCurrentVoice() {
        return _recordingSelectedVoice.getValue();
    }

    //VMUSDKStatsHelperBroken TODO: On VoIP NDK Code modify this java value when SampleRate is specified
    private static int VoIPSampleRate = 0;
    public static int getVoIPSampleRate() {
        return VoIPSampleRate;
    }
}
