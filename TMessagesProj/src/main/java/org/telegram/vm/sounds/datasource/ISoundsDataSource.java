package org.telegram.vm.sounds.datasource;

import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.models.tuna.SoundAutocomplete;
import org.telegram.vm.models.tuna.SoundCategory;
import org.telegram.vm.models.tuna.SoundsPage;

import java.util.function.Function;

public interface ISoundsDataSource {

    void getCategories(Function<SoundCategory[], Void> onSuccess);

    void getCategoriesWithSoundCount(Function<SoundCategory[], Void> onSuccess);

    void getSoundById(String id,Function<Sound, Void> onSuccess);

    void getSoundAutocomplete(String search, int size, Function<SoundAutocomplete, Void> onSuccess);

    void searchSounds(int size, int page, String search, String categoryFilter, String tagFilter, String sort, String order, Function<SoundsPage, Void> onSuccess);

}
