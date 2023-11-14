package org.telegram.vm.remoteconfig.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoteVoiceCategories {

    public RemoteVoiceCategory[] categories;

    public RemoteVoiceCategories(RemoteVoiceCategory[] categories) {
        this.categories = categories;
    }

    public static RemoteVoiceCategories fromJson(String json){
        try {
            JSONArray categories = new JSONArray(json);
            RemoteVoiceCategory[] remoteVoiceCategories = new RemoteVoiceCategory[categories.length()];

            for (int i=0;i<categories.length();i++){
                JSONObject category = categories.getJSONObject(i);
                JSONArray voices = category.getJSONArray("voices");
                String[] voicesArray = new String[voices.length()];
                for (int j=0;j<voices.length();j++){
                    voicesArray[j] = voices.getString(j);
                }
                remoteVoiceCategories[i] = new RemoteVoiceCategory(category.getString("categoryKey"),category.getString("color"),category.getString("icon"),voicesArray);
            }

            return new RemoteVoiceCategories(remoteVoiceCategories);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
