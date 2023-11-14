package org.telegram.vm.models.tuna;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SoundAutocomplete {

    public String[] items;

    public SoundAutocomplete(String[] items) {
        this.items = items;
    }

    public static SoundAutocomplete fromJson(String json){
        try {
            JSONObject autocomplete = new JSONObject(json);
            String[] items = new String[autocomplete.getJSONArray("items").length()];

            JSONArray itemsArray = autocomplete.getJSONArray("items");

            for (int i=0;i<itemsArray.length();i++){
                items[i] = itemsArray.getString(i);
            }

            return new SoundAutocomplete(items);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
