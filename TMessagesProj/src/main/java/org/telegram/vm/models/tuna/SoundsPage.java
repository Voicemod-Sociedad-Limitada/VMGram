package org.telegram.vm.models.tuna;

import org.json.JSONException;
import org.json.JSONObject;

public class SoundsPage {

    public int total, size, page, lastPage;
    public Sound[] items;

    public SoundsPage(int total, int size, int page, int lastPage, Sound[] items) {
        this.total = total;
        this.size = size;
        this.page = page;
        this.lastPage = lastPage;
        this.items = items;
    }

    public static SoundsPage fromJson(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

            int total = jsonObject.getInt("total");
            int size = jsonObject.getInt("size");
            int page = jsonObject.getInt("page");
            int lastPage = jsonObject.getInt("lastPage");

            Sound[] sounds = new Sound[jsonObject.getJSONArray("items").length()];
            for (int i=0;i<sounds.length;i++){
                sounds[i] = Sound.fromJson(jsonObject.getJSONArray("items").getJSONObject(i).toString());
            }

            return new SoundsPage(total, size, page, lastPage, sounds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
