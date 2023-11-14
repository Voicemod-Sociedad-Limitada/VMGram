package org.telegram.vm.adapters.models;

import java.util.Objects;

/**
 * This model is a representation of a Voicemod Sound but it is for UI purposes.
 */
public class UISound {
    public String id;
    public String name;
    public String author;
    public String imageUrl;
    public String soundPathMp3;
    public String soundPathOgg;


    public UISound(String id, String name, String imageUrl, String author, String soundPathMp3, String soundPathOgg) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.author = author;
        this.soundPathMp3 = soundPathMp3;
        this.soundPathOgg = soundPathOgg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UISound sound = (UISound) o;
        return Objects.equals(id, sound.id) && Objects.equals(name, sound.name) &&
                Objects.equals(author, sound.author) && Objects.equals(imageUrl, sound.imageUrl)
                && Objects.equals(soundPathMp3, sound.soundPathMp3) && Objects.equals(soundPathOgg, sound.soundPathOgg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author, imageUrl, soundPathMp3, soundPathOgg);
    }
}
