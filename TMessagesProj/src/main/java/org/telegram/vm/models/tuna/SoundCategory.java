package org.telegram.vm.models.tuna;

import org.json.JSONException;
import org.json.JSONObject;

public class SoundCategory {

    public String name;
    public int soundCount;

    SoundCategory(String name, int soundCount) {
        this.name = name;
        this.soundCount = soundCount;
    }

    public static SoundCategory fromJson(String json) {
        try {
            JSONObject category = new JSONObject(json);

            return new SoundCategory(
                    category.getString("name"),
                    category.has("soundCount") ? category.getInt("soundCount") : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
