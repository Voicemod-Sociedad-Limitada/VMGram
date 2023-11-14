package org.telegram.vm.sounds.repository;

public interface ISoundsRepository {

    void fetchSoundCategories();
    void fetchTrendingSounds(int page);
    void fetchSounds(String category, String search, int page);
    void fetchSounds(String category, int page);
    void fetchSound(String soundId);

}
