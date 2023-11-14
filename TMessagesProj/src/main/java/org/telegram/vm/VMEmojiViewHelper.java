package org.telegram.vm;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.telegram.messenger.FileLoader;
import org.telegram.vm.adapters.models.UISound;
import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.models.tuna.SoundCategory;
import org.telegram.vm.models.tuna.SoundsPage;
import org.telegram.vm.sounds.datasource.TunaSoundsDataSource;
import org.telegram.vm.sounds.repository.SoundsRepository;
import org.telegram.vm.utils.IOUtil;
import org.telegram.vm.utils.SoundUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class VMEmojiViewHelper {
    private final TunaSoundsDataSource tunaDataSource = new TunaSoundsDataSource();
    private final SoundsRepository soundsRepository = new SoundsRepository(tunaDataSource);

    public LiveData<SoundsPage> getSoundsLiveData(String categorySelected, String searchTerm, int page) {
        soundsRepository.fetchSounds(categorySelected, searchTerm,"desc", page);
        return soundsRepository.currentSounds;
    }

    public LiveData<SoundsPage> getSoundsLiveData(String categorySelected, int page) {
        soundsRepository.fetchSounds(categorySelected, "", "desc", page);
        return soundsRepository.currentSounds;
    }

    public LiveData<SoundCategory[]> getSoundCategoriesLiveData() {
        soundsRepository.fetchSoundCategories();
        return soundsRepository.soundCategories;
    }

    public LiveData<String[]> getSoundsAutocompleteLiveData(String searchTerm) {
        soundsRepository.fetchSoundsAutocomplete(searchTerm);
        return soundsRepository.currentSearchSuggestions;
    }

    public LiveData<SoundsPage> getVoicemodSelectionSoundsLiveData() {
        MutableLiveData<SoundsPage> liveData = new MutableLiveData<>();
        Sound[] sounds = soundsRepository.getVoicemodSelectionsSounds();
        if (sounds != null) {
            liveData.setValue(new SoundsPage(
                    sounds.length,
                    sounds.length,
                    1,
                    1,
                    sounds
            ));
        }
        return liveData;
    }

    public void createWaveFormFromOggFile(File selectedSoundFile, UISound selectedSound, Function<short[], Void> onSuccess) {
        Uri destination = Uri.parse("file://" + FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE)
                + "/vmCachedSounds/" + selectedSound.id + "_"+System.currentTimeMillis()+".pcm");

        SoundUtils.convertToPCM(selectedSoundFile.getPath(), destination.getPath(),
                (pcmFile) -> {
                    byte[] soundBytes = new byte[0];
                    try {
                        soundBytes = IOUtil.readFile(pcmFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    short[] soundShortBytes = new short[soundBytes.length / 2];
                    for (int i = 0; i < soundBytes.length / 2; i++) {
                        soundShortBytes[i] = ((short) ((soundBytes[i * 2] & 0xff) | (soundBytes[i * 2 + 1] << 8)));
                    }
                    onSuccess.apply(soundShortBytes);
                    return null;
                },
                (file) -> {
                    return null;
                });
    }
}
