package org.telegram.vm;

public class VoicemodUSDKStats {
    public float audioCallbackDuration;
    public float currentProcessBufferDuration;
    public float aiNodeAverageCallDuration;
    public long aiNodeCalls;
    public long aiNodeDropouts;
    public long framesPerBuffer;

    VoicemodUSDKStats(
            float audioCallbackDuration,
            float currentProcessBufferDuration,
            float aiNodeAverageCallDuration,
            long aiNodeCalls,
            long aiNodeDropouts,
            long framesPerBuffer
    ) {
        this.audioCallbackDuration = audioCallbackDuration;
        this.currentProcessBufferDuration = currentProcessBufferDuration;
        this.aiNodeAverageCallDuration = aiNodeAverageCallDuration;
        this.aiNodeCalls = aiNodeCalls;
        this.aiNodeDropouts = aiNodeDropouts;
        this.framesPerBuffer = framesPerBuffer;
    }
}
