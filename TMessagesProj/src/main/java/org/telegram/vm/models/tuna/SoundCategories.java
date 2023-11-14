package org.telegram.vm.models.tuna;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SoundCategories {

    public SoundCategory[] items;

    SoundCategories(SoundCategory[] items){
        this.items = items;
    }

    public static SoundCategories fromJson(String json){
        try {
            JSONObject categoriesObject = new JSONObject(json);
            JSONArray categoriesArray = categoriesObject.getJSONArray("items");
            SoundCategory[] soundCategories = new SoundCategory[categoriesArray.length()];

            for (int i=0;i<categoriesArray.length();i++){
                JSONObject category = categoriesArray.getJSONObject(i);
                soundCategories[i] = SoundCategory.fromJson(category.toString());
            }

            return new SoundCategories(soundCategories);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
