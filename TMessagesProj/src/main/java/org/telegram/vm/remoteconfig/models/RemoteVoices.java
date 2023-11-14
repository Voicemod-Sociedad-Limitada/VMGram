package org.telegram.vm.remoteconfig.models;

import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;

import net.voicemod.vmgramservice.VMGramClient;
import net.voicemod.vmgramservice.VmgsResultAidl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.vm.models.Voice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RemoteVoices {

    private static final String TAG = "RemoteVoices";

    public Voice[] voicesFree;

    RemoteVoices(Voice[] voicesFree){
        this.voicesFree = voicesFree;
    }

    static final String criticalErrorExceptionServiceCommunication =
        "An unexpected error occurred when trying to call VMGramService::getVoiceNames";

    public static RemoteVoices fromJson(String json){
        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray voices = jsonObject.getJSONArray("voicesFree");

            HashSet<String> vmgramServiceSupportedVoicesSet = null;
            VMGramClient vmGramClient = VMGramClient.getInstance();
            try {
                ArrayList<String> vmgramServiceSupportedVoices = new ArrayList<>();
                Pair<VmgsResultAidl, RemoteException> result =
                        vmGramClient.getVoiceNames(vmgramServiceSupportedVoices).get();
                if (result.first != VmgsResultAidl.vcmdNoError) {
                    final String detailedError = criticalErrorExceptionServiceCommunication
                        + " VmgsResultAidl:" + result.toString();
                    Log.wtf(TAG, detailedError);
                    throw new RuntimeException(TAG + ":" + detailedError);
                }
                vmgramServiceSupportedVoicesSet = new HashSet<>(vmgramServiceSupportedVoices);
            } catch (Exception ee) {
                final String detailedError = criticalErrorExceptionServiceCommunication
                    + " ExecutionException:" + ee.getClass().getSimpleName() + ":" + ee.getMessage();
                Log.wtf(TAG, detailedError);
                throw new RuntimeException(TAG + ":" + detailedError);
            }

            ArrayList<Voice> voicesFreeLocal = new ArrayList<Voice>();
            for (int i=0;i<voices.length();i++){
                JSONObject voice = voices.getJSONObject(i);
                if(vmgramServiceSupportedVoicesSet.contains(voice.getString("id"))){
                    voicesFreeLocal.add(
                        new Voice(voice.getString("id"),voice.getString("name"),voice.getBoolean("enabled"),voice.getBoolean("hasBackgroundSound"))
                    );
                }
            }

            return new RemoteVoices(voicesFreeLocal.toArray(new Voice[0]));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
