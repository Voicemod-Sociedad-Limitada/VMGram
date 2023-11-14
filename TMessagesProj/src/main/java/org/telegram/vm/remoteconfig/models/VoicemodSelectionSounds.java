package org.telegram.vm.remoteconfig.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.vm.models.tuna.Sound;

public class VoicemodSelectionSounds {
    public Sound[] soundSelection;

    public VoicemodSelectionSounds(Sound[] soundSelection) {
        this.soundSelection = soundSelection;
    }

    public static VoicemodSelectionSounds fromJson(String json) {
        try {
            JSONObject response = new JSONObject(json);
            JSONArray soundSelection = response.getJSONArray("soundSelection");
            Sound[] sounds = new Sound[soundSelection.length()];
            for (int i = 0; i < soundSelection.length(); i++) {
                sounds[i] = Sound.fromJson(soundSelection.getJSONObject(i).toString());
            }
            return new VoicemodSelectionSounds(sounds);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
