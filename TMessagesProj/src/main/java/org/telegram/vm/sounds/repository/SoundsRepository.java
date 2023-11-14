package org.telegram.vm.sounds.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.models.tuna.SoundCategory;
import org.telegram.vm.models.tuna.SoundsPage;
import org.telegram.vm.remoteconfig.firebase.FirebaseRemoteConfigRepository;
import org.telegram.vm.sounds.datasource.ISoundsDataSource;

public class SoundsRepository implements ISoundsRepository{

    private static final int PAGE_SIZE = 20;

    ISoundsDataSource dataSource;

    private final FirebaseRemoteConfigRepository firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository.getInstance();

    private final MutableLiveData<SoundCategory[]> _soundCategories = new MutableLiveData<>();
    public LiveData<SoundCategory[]> soundCategories = _soundCategories;

    private final MutableLiveData<SoundsPage> _currentSounds = new MutableLiveData<>();
    public LiveData<SoundsPage> currentSounds = _currentSounds;

    private final MutableLiveData<Sound> _currentSound = new MutableLiveData<>();
    public LiveData<Sound> currentSound = _currentSound;

    private final MutableLiveData<String[]> _currentSearchSuggestions = new MutableLiveData<>();
    public LiveData<String[]> currentSearchSuggestions = _currentSearchSuggestions;

    public SoundsRepository(ISoundsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void fetchSoundCategories(){
        dataSource.getCategories((categories) -> {
            _soundCategories.setValue(categories);
            return null;
        });
    }

    /***
     * There's no "trending" endpoint so this is a temporary workaround.
     */
    public void fetchTrendingSounds(int page){
        dataSource.searchSounds(PAGE_SIZE, page, "", "", "", "download", "desc",(pageData) -> {
            _currentSounds.setValue(pageData);
            return null;
        });
    }

    public void fetchSounds(String category, String search, String order, int page) {
        dataSource.searchSounds(PAGE_SIZE, page, search, category, "", "", order, (pageData) -> {
            _currentSounds.setValue(pageData);
            return null;
        });
    }

    public void fetchSounds(String category, int page){
        dataSource.searchSounds(PAGE_SIZE, page, "", category, "", "download", "desc", (pageData) -> {
            _currentSounds.setValue(pageData);
            return null;
        });
    }

    public void fetchSounds(String category, String search, int page){
        dataSource.searchSounds(PAGE_SIZE, page, search, category, "", "", "",(pageData) -> {
            _currentSounds.setValue(pageData);
            return null;
        });
    }

    public void fetchSound(String soundId) {
        dataSource.getSoundById(soundId, (sound) -> {
            _currentSound.setValue(sound);
            return null;
        });
    }

    public void fetchSoundsAutocomplete(String searchTerm) {
        dataSource.getSoundAutocomplete(searchTerm, 6, (soundAutocomplete) -> {
            _currentSearchSuggestions.setValue(soundAutocomplete.items);
            return null;
        });
    }

    public Sound[] getVoicemodSelectionsSounds() {
        return firebaseRemoteConfigRepository.getVoicemodeSelectionSounds();
    }
}
