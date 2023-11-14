package org.telegram.vm.models.tuna;

import org.json.JSONException;
import org.json.JSONObject;

public class Sound {

    public String id;
    public SoundOwner owner;
    public String title;
    public String category;
    public String path;
    public String oggPath;
    public String imagePath;
    public String description;

    public Sound(String id, SoundOwner owner, String title, String category, String path, String oggPath, String imagePath, String description) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.category = category;
        this.path = path;
        this.oggPath = oggPath;
        this.imagePath = imagePath;
        this.description = description;
    }

    public static Sound fromJson(String json) {
        try {
            JSONObject sound = new JSONObject(json);

            return new Sound(
                    sound.getString("id"),
                    SoundOwner.fromJson(sound.getJSONObject("owner").toString()),
                    sound.getString("title"),
                    sound.getString("category"),
                    sound.getString("path"),
                    sound.getString("oggPath"),
                    sound.getString("imagePath"),
                    sound.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class SoundOwner {

        public String id,name, slug;

        public SoundOwner(String id, String name, String slug) {
            this.id = id;
            this.name = name;
            this.slug = slug;
        }

        public static SoundOwner fromJson(String json) {
            try {
                JSONObject owner = new JSONObject(json);
                return new SoundOwner(
                        owner.getString("id"),
                        owner.getString("name"),
                        owner.getString("slug")
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}


