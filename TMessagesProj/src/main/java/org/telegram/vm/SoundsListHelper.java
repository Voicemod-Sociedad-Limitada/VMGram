package org.telegram.vm;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.vm.adapters.SoundBoardAdapter;
import org.telegram.vm.adapters.SoundCategoriesAdapter;
import org.telegram.vm.adapters.models.UISound;
import org.telegram.vm.adapters.models.UISoundCategory;
import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.models.tuna.SoundCategory;
import org.telegram.vm.models.tuna.SoundsPage;
import org.telegram.vm.sounds.suggestions.SuggestionsAdapter;
import org.telegram.vm.tracking.EntryPoint;
import org.telegram.vm.utils.TunaUtils;
import org.telegram.vm.utils.download.DownloadInProgress;
import org.telegram.vm.utils.download.DownloadResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SoundsListHelper {

    private EmojiView.EmojiViewDelegate delegate;
    private FrameLayout soundBoardContainer;
    private RecyclerView soundBoardGridView;
    private GridLayoutManager soundBoardGridLayoutManager;
    private SoundBoardAdapter soundBoardAdapter;
    private VMEmojiViewHelper vmEmojiViewHelper = new VMEmojiViewHelper();
    private ArrayList<UISound> sounds = new ArrayList<>();
    private ArrayList<UISoundCategory> soundCategories = new ArrayList<>();
    private boolean soundCategoryChanged = false;
    private SoundCategoriesAdapter soundCategoriesAdapter;
    private boolean soundSpinnerOpenedByUser = false;
    private final UISoundCategory defaultCategory = new UISoundCategory(LocaleController.getString("DefaultSpinnerSoundCategory",
            R.string.DefaultSpinnerSoundCategory));
    private final UISoundCategory voicemodSelectionSoundsCategory = new UISoundCategory(LocaleController.getString("VoicemodSelectionSoundsCategory",
            R.string.VoicemodSelectionSoundsCategory));
    private final UISoundCategory featuredCategory = new UISoundCategory(LocaleController.getString("FeaturedCategory",
            R.string.FeaturedCategory));
    private UISoundCategory selectedSoundCategory, previousSelectedSoundCategory;
    private String selectedSearchTerm = null;
    private SoundsPage currentSoundsPage;
    private boolean isGettingSoundPage = false;
    private UISound selectedSound;
    private File selectedSoundFile;
    private MediaPlayer mediaPlayer;
    private long dialogId;
    private short[] selectedSoundBytes;
    private boolean isDownloadingTunaSound = false;
    private View suggestionView, searchAndCategoriesSpinner;
    private View searchDoneView;
    private EditText suggestionEditText;
    private final ArrayList<UISound> currentlySendingSounds = new ArrayList<>();
    private Context context;
    private OnSoundListListener onSoundListListener;
    private int downloadIdCounter = 0;
    private ArrayList<DownloadInProgress> currentDownloads = new ArrayList<>();

    private EntryPoint entryPoint;

    public interface OnSoundListListener{
        void soundSelected(File selectedFile, short[] soundBytes, UISound selectedSound);
        void soundDownloadCancelled(UISound selectedSound);
        void onCurrentSoundStopped();
        void onScrollChangeListener(int scroll);
        void onHideSearchKeyboard();
    }

    public void setOnSoundSelected(OnSoundListListener onSoundListListener) {
        this.onSoundListListener = onSoundListListener;
    }

    public SoundsListHelper(Context context, EntryPoint entryPoint) {
        this.context = context;
        this.entryPoint = entryPoint;
    }

    public void setDelegate(EmojiView.EmojiViewDelegate delegate) {
        this.delegate = delegate;
    }

    public FrameLayout getSoundboardView(boolean isBottomSheet) {
        soundBoardContainer = new FrameLayout(context);

        soundBoardGridView = new RecyclerView(context);

        soundBoardGridView.setNestedScrollingEnabled(false);
        soundBoardGridView.setClipToPadding(false);
        soundBoardGridLayoutManager = new GridLayoutManager(context, !isBottomSheet ? 4 : 5);
        soundBoardGridView.setLayoutManager(soundBoardGridLayoutManager);
        soundBoardGridView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = AndroidUtilities.dp(6);
                outRect.bottom = AndroidUtilities.dp(!isBottomSheet ? 10 : 15);
                outRect.top = 0;
                outRect.right = AndroidUtilities.dp(6);
            }
        });
        soundBoardGridView.setPadding(AndroidUtilities.dp(6), AndroidUtilities.dp(!isBottomSheet ? 0 : 5),
                AndroidUtilities.dp(6), AndroidUtilities.dp(50));
        soundBoardGridView.setOverScrollMode(RecyclerListView.OVER_SCROLL_NEVER);
        ((SimpleItemAnimator) soundBoardGridView.getItemAnimator()).setSupportsChangeAnimations(false);
        soundBoardAdapter = new SoundBoardAdapter(context, sounds, currentlySendingSounds, isBottomSheet);
        soundBoardGridView.setAdapter(soundBoardAdapter);

        setSoundboardItemClickListener();

        LinearLayout soundBoardLinearLayout = new LinearLayout(context);
        soundBoardLinearLayout.setOrientation(LinearLayout.VERTICAL);
        View spinnerView = createSoundboardCategoriesSpinner();
        soundBoardLinearLayout.addView(spinnerView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 0));

        TextView instructionsTv = new TextView(context);
        instructionsTv.setTextColor(ContextCompat.getColor(context, R.color.vm_grey_dark_5));
        instructionsTv.setText(LocaleController.getString("SoundboardsInstructionSubtitle",
                R.string.SoundboardsInstructionSubtitle));
        instructionsTv.setTextSize(11);
        instructionsTv.setPadding(25, 0, 16, 25);
        soundBoardLinearLayout.addView(instructionsTv);

        soundBoardLinearLayout.addView(soundBoardGridView);

        NestedScrollView scrollView = new NestedScrollView(context);
        scrollView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                soundBoardAdapter.sendParentEventToCurrentSelectedHolder(event);
            }
            return false;
        });

        scrollView.addView(soundBoardLinearLayout);

        setSoundsListScrollListener(scrollView);

        soundBoardContainer.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        mediaPlayer = new MediaPlayer();
        return soundBoardContainer;
    }

    private void setSoundboardItemClickListener() {

        soundBoardAdapter.setOnStopClickedListener((nothing) -> {
            onSoundListListener.onCurrentSoundStopped();
            return null;
        });

        soundBoardAdapter.setOnItemClickListener((view, position) -> {
            final File soundFile = selectedSoundFile;
            final UISound sound = selectedSound;
            final short[] soundBytes = selectedSoundBytes;

            boolean allowParallelDownloads = !soundBoardAdapter.isBottomSheet();

            //I HAVE DISABLED THE CACHED DOWNLOADS BECAUSE THEY INCREASE THE CHANCE OF A RACE CONDITION
            //REENABLE THIS IF WE CHANGE THE FEATURE AND NO RACE CONDITIONS CAN ARISE.
            boolean cacheEnabled = false;

            //Same sound selected
            if (cacheEnabled && sound == sounds.get(position) && soundBytes != null) {
                //Its not necessary to download it again so we send it directly
                if (!allowParallelDownloads) cancelAllDownloads();
                onSoundListListener.soundSelected(soundFile, soundBytes, sound);
            } else {
                //Different sound selected but is still being downloaded
                if (sound == sounds.get(position) && isDownloadingTunaSound) {
                    return;
                }

                //Download new sound
                final UISound newSound = sounds.get(position);
                selectedSound = newSound;
                selectedSoundBytes = null;
                isDownloadingTunaSound = true;
                currentlySendingSounds.add(newSound);
                soundBoardAdapter.notifyItemChanged(position);
                downloadTunaSound(newSound, (nothing) -> {
                    currentlySendingSounds.remove(newSound);
                    if (!soundBoardAdapter.isBottomSheet()) {
                        soundBoardAdapter.notifyDataSetChanged();
                    } else {
                        soundBoardAdapter.notifyItemChanged(position);
                    }
                    return null;
                }, allowParallelDownloads);
            }
        });

        soundBoardAdapter.setOnItemLongClickListener((view, position) -> {
            final UISound newSound;
            final UISound sound = selectedSound;
            int previousSoundPosition = sounds.indexOf(sound);

            if (sound != null) {
                mediaPlayer.stop();
                if (previousSoundPosition != position) {
                    newSound = sounds.get(position);
                    selectedSound = newSound;
                    soundBoardAdapter.notifyItemChanged(previousSoundPosition);
                } else {
                    newSound = sound;
                }
            } else {
                newSound = sounds.get(position);
                selectedSound = newSound;
            }

            soundBoardAdapter.notifyItemChanged(position);

            VMUSDKStatsHelper.onSoundPreviewed(entryPoint, newSound.name);

            playSound(newSound);

            return false;
        });

        soundBoardAdapter.setOnItemLongClickCancelListener((uiSound) -> {
            final UISound sound = selectedSound;

            if (sound != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                int previousSoundPosition = sounds.indexOf(sound);

                selectedSound = null;
                selectedSoundBytes = null;
                soundBoardAdapter.soundStoppedPlaying(soundBoardGridView.findViewHolderForAdapterPosition(previousSoundPosition));
                soundBoardAdapter.notifyItemChanged(previousSoundPosition);
            }

            return null;
        });
    }

    private void playSound(UISound sound) {
        try {
            final int soundIndex = sounds.indexOf(sound);

            soundBoardAdapter.setMediaPlayer(mediaPlayer);
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(sound.soundPathMp3);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.reset();
                soundBoardAdapter.soundStoppedPlaying(soundBoardGridView.findViewHolderForAdapterPosition(soundIndex));
            });
        } catch (IOException e) {
            Log.e("Tuna", "Error playing sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized int getDownloadId(){
        return downloadIdCounter++;
    }

    private void downloadTunaSound(UISound uiSound, Function onFinished) {
        TunaUtils.downloadTunaSoundAsOgg(uiSound,
                (file) -> {
                    selectedSoundFile = file;

                    vmEmojiViewHelper.createWaveFormFromOggFile(file, uiSound, (audioBytes -> {
                        selectedSoundBytes = audioBytes;
                        isDownloadingTunaSound = false;
                        onSoundListListener.soundSelected(file, audioBytes, uiSound);
                        currentlySendingSounds.remove(uiSound);
                        onFinished.apply(null);
                        return null;
                    }));

                    return null;
                },
                (error) -> {
                    Log.d("Tuna", "Error downloading sound" + error);
                    isDownloadingTunaSound = false;
                    selectedSound = null;
                    selectedSoundBytes = null;
                    currentlySendingSounds.remove(selectedSound);
                    currentlySendingSounds.remove(uiSound);
                    return null;
                }
        );
    }

    //Only required if we use cached downloads
    private void cancelAllDownloads(){
        for (DownloadInProgress item : currentDownloads) {
            item.cancelled = true;
        }
    }

    private synchronized void downloadTunaSound(UISound uiSound, Function onFinished, boolean allowParallelDownloads) {
        DownloadInProgress newDownload = new DownloadInProgress(getDownloadId(), uiSound, false);
        if (!allowParallelDownloads) {
            for (DownloadInProgress item : currentDownloads) {
                item.cancelled = true;
                currentlySendingSounds.remove(item.uiSound);
                onFinished.apply(null);
            }
        }
        currentDownloads.add(newDownload);

        TunaUtils.downloadTunaSoundAsOgg(newDownload.id, uiSound,
                (DownloadResult result) -> {
                    List<DownloadInProgress> downloadsInProgress = currentDownloads.stream().filter(item -> item.id == result.id).collect(Collectors.toList());
                    DownloadInProgress download = (!downloadsInProgress.isEmpty()) ? downloadsInProgress.get(0) : null;

                    if (allowParallelDownloads || download != null && !download.cancelled) {
                        File currentFile = result.file;
                        selectedSoundFile = currentFile;

                        vmEmojiViewHelper.createWaveFormFromOggFile(currentFile, uiSound, (audioBytes -> {
                            selectedSoundBytes = audioBytes;
                            isDownloadingTunaSound = false;
                            onSoundListListener.soundSelected(currentFile, audioBytes, uiSound);
                            currentlySendingSounds.remove(uiSound);
                            onFinished.apply(null);
                            return null;
                        }));
                    }

                    if (download != null) currentDownloads.remove(download);

                    return null;
                },
                (error) -> {
                    Log.d("Tuna", "Error downloading sound" + error);
                    isDownloadingTunaSound = false;
                    selectedSound = null;
                    selectedSoundBytes = null;
                    currentlySendingSounds.remove(selectedSound);
                    currentlySendingSounds.remove(uiSound);
                    return null;
                }
        );
    }

    private void setSoundsListScrollListener(NestedScrollView scrollView) {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(0).getBottom() - 50 <= (scrollView.getHeight() + scrollY)) {
                if (!isGettingSoundPage) {
                    isGettingSoundPage = true;
                    if (currentSoundsPage.page < currentSoundsPage.lastPage)
                        getSoundPage(currentSoundsPage.page + 1);
                }
            }
            onSoundListListener.onScrollChangeListener((-(oldScrollY - scrollY)));
        });
    }

    public void showSearchDoneView() {
        TextView searchText = (TextView) searchDoneView.findViewById(R.id.searchText);
        if (selectedSearchTerm.contains("\n")) {
            selectedSearchTerm = selectedSearchTerm.replace("\n", "");
        }
        searchText.setText(selectedSearchTerm);
        searchDoneView.setVisibility(VISIBLE);
    }

    public void hideSearchDoneView() {
        searchDoneView.setVisibility(GONE);
    }

    public void showSuggestionView() {
        suggestionView.setVisibility(VISIBLE);
    }

    public boolean isSearchDoneSuggestionViewVisible() {
        return searchDoneView.getVisibility() == VISIBLE;
    }

    public void hideSuggestionView() {
        suggestionView.setVisibility(GONE);
        suggestionEditText.clearFocus();
        onSoundListListener.onHideSearchKeyboard();
    }

    public boolean isSuggestionViewVisible() {
        return suggestionView.getVisibility() == VISIBLE;
    }

    public void hideSearchAndCategoriesSpinner() {
        searchAndCategoriesSpinner.setVisibility(GONE);
    }

    public void showSearchAndCategoriesSpinner() {
        searchAndCategoriesSpinner.setVisibility(VISIBLE);
    }

    public boolean thereIsSelectedSearchTerm() {
        return selectedSearchTerm != null;
    }

    public View createSoundboardCategoriesSpinner() {
        Context newContext = new ContextThemeWrapper(context, R.style.SpinnerTheme);

        View searchGlobalContainer = LayoutInflater.from(newContext)
                .inflate(R.layout.sound_search_bar, null);
        Spinner spinner = searchGlobalContainer.findViewById(R.id.categories_spinner);

        searchAndCategoriesSpinner = searchGlobalContainer.findViewById(R.id.searchAndCategoriesSpinner);

        //VOICEMOD TUNA SUGGESTIONS (AUTOCOMPLETE)
        SuggestionsAdapter adapter = new SuggestionsAdapter();
        adapter.setOnSuggestionClicked((suggestion) -> {
            onSearchTermSelected(suggestion);
            hideSuggestionView();
            hideSearchAndCategoriesSpinner();
            showSearchDoneView();
            suggestionEditText.setText(suggestion);

            return null;
        });

        RecyclerView suggestionsList = searchGlobalContainer.findViewById(R.id.suggestionList);
        suggestionsList.setLayoutManager(new LinearLayoutManager(context));
        suggestionsList.setAdapter(adapter);

        ImageView searchButton = searchGlobalContainer.findViewById(R.id.searchIcon);
        suggestionView = searchGlobalContainer.findViewById(R.id.searchSuggestionsView);
        searchDoneView = searchGlobalContainer.findViewById(R.id.searchResultsContainer);

        suggestionEditText = searchGlobalContainer.findViewById(R.id.suggestionInput);

        searchButton.setOnClickListener((view) -> {
            if (delegate != null) //calling to setDelegate is required previously
                delegate.onSearchOpenClose(1);
            showSuggestionView();
            hideSearchAndCategoriesSpinner();
            suggestionEditText.requestFocus();
            AndroidUtilities.showKeyboard(suggestionEditText);
        });

        ImageView cancelSearchButton = searchGlobalContainer.findViewById(R.id.cancelSearch);
        cancelSearchButton.setOnTouchListener((view, event) -> {
            hideSuggestionView();
            showSearchAndCategoriesSpinner();
            suggestionEditText.setText("");
            return true;
        });


        suggestionEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
                    s.delete(s.length() - 1, s.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
                    onSearchTermSelected(suggestionEditText.getText().toString());
                    hideSuggestionView();
                    hideSearchAndCategoriesSpinner();
                    showSearchDoneView();
                }

                if (s.length() >= 3) {
                    LiveData<String[]> suggestionsLiveData = vmEmojiViewHelper.getSoundsAutocompleteLiveData(s.toString());

                    if (!suggestionsLiveData.hasActiveObservers()) {
                        suggestionsLiveData.observeForever((suggestions) -> {
                            adapter.setDataSet(suggestions);
                        });
                    }
                } else {
                    adapter.setDataSet(new String[]{});
                }
            }
        });

        suggestionEditText.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (delegate != null)
                    delegate.onSearchOpenClose(1);
                suggestionEditText.requestFocus();
                AndroidUtilities.showKeyboard(suggestionEditText);
            }
            return true;
        });

        //If we click on the search done view, we open again the search suggestion view.
        searchDoneView.findViewById(R.id.searchText).setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showSuggestionView();
                hideSearchDoneView();
                if (delegate != null)
                    delegate.onSearchOpenClose(1);
                suggestionEditText.requestFocus();
                AndroidUtilities.showKeyboard(suggestionEditText);
            }
            return true;
        });

        //Clear search
        searchDoneView.findViewById(R.id.cancelSearch).setOnClickListener((view) -> {
            hideSearchDoneView();
            hideSuggestionView();
            showSearchAndCategoriesSpinner();
            final int previousSize = sounds.size();
            sounds.clear();
            soundBoardAdapter.notifyItemRangeRemoved(0, previousSize);
            selectedSearchTerm = null;
            selectedSoundCategory = previousSelectedSoundCategory;
            getSoundPage(1);
            suggestionEditText.setText("");
        });

        //Make smaller spinner dropdown
        changeHeightSpinnerDropdown(spinner);

        soundCategoriesAdapter = new SoundCategoriesAdapter(context, soundCategories, spinner);
        spinner.setAdapter(soundCategoriesAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onCategorySelected(soundCategories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        LiveData<SoundCategory[]> soundCategoriesLiveData = vmEmojiViewHelper.getSoundCategoriesLiveData();
        soundCategoriesLiveData.observeForever(new Observer<SoundCategory[]>() {
            @Override
            public void onChanged(SoundCategory[] tunaSoundCategories) {
                soundCategoriesLiveData.removeObserver(this);
                soundCategories.clear();
                if (tunaSoundCategories != null) {
                    if (!soundCategoryChanged) {
                        soundCategories.add(defaultCategory);
                    } else {
                        soundCategories.add(voicemodSelectionSoundsCategory);
                    }
                    soundCategories.add(featuredCategory);
                    for (SoundCategory soundCategory : tunaSoundCategories) {
                        soundCategories.add(new UISoundCategory(soundCategory.name));
                    }
                    soundCategoriesAdapter.notifyDataSetChanged();
                }
            }
        });

        spinner.setOnTouchListener((view, motionEvent) -> {
            soundSpinnerOpenedByUser = true;
            return false;
        });

        return searchGlobalContainer;
    }

    private void changeHeightSpinnerDropdown(Spinner spinner) {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);
            popupWindow.setHeight(AndroidUtilities.dp((int) (42.0 * 3.5)));
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
        }
    }

    private void onCategorySelected(UISoundCategory soundCategory) {
        if (!soundCategoryChanged && soundSpinnerOpenedByUser) {
            soundCategories.set(0, voicemodSelectionSoundsCategory);
            soundCategoriesAdapter.notifyDataSetChanged();
            soundCategoryChanged = true;
        }
        selectedSoundCategory = soundCategory;
        selectedSearchTerm = null;
        final int previousSize = sounds.size();
        sounds.clear();
        soundBoardAdapter.notifyItemRangeRemoved(0, previousSize);
        getSoundPage(1);
    }

    private void onSearchTermSelected(String searchTerm) {
        previousSelectedSoundCategory = (selectedSoundCategory != null) ? selectedSoundCategory : previousSelectedSoundCategory;
        selectedSoundCategory = null;
        selectedSearchTerm = searchTerm;
        final int previousSize = sounds.size();
        sounds.clear();
        soundBoardAdapter.notifyItemRangeRemoved(0, previousSize);
        getSoundPage(1);
    }

    private void getSoundPage(int soundPageNumber) {
        LiveData<SoundsPage> soundsLiveData;

        if (selectedSearchTerm != null) {
            soundsLiveData = vmEmojiViewHelper.getSoundsLiveData("", selectedSearchTerm, soundPageNumber);
        } else {
            String categoryToSearch = selectedSoundCategory.name;
            if (selectedSoundCategory == voicemodSelectionSoundsCategory ||
                    selectedSoundCategory == defaultCategory) {
                soundsLiveData = vmEmojiViewHelper.getVoicemodSelectionSoundsLiveData();
                if (soundsLiveData.getValue() == null) {
                    categoryToSearch = "";
                    soundsLiveData = vmEmojiViewHelper.getSoundsLiveData(categoryToSearch, soundPageNumber);
                }
            } else if (selectedSoundCategory == featuredCategory) {
                categoryToSearch = "";
                soundsLiveData = vmEmojiViewHelper.getSoundsLiveData(categoryToSearch, soundPageNumber);
            } else {
                soundsLiveData = vmEmojiViewHelper.getSoundsLiveData(categoryToSearch, soundPageNumber);
            }
        }

        if (!soundsLiveData.hasActiveObservers()) {
            soundsLiveData.observeForever(soundsPage -> {
                currentSoundsPage = soundsPage;
                onSoundsPageReceived(soundsPage);
                isGettingSoundPage = false;
            });
        }
    }

    private void onSoundsPageReceived(SoundsPage soundsPage) {
        final int previousSize = sounds.size();

        if (soundsPage != null && soundsPage.items.length != 0) {
            for (Sound sound : soundsPage.items) {
                sounds.add(new UISound(sound.id, sound.title, sound.imagePath, sound.owner != null ? sound.owner.name : null, sound.path, sound.oggPath));
            }

            soundBoardAdapter.notifyItemRangeInserted(previousSize, soundsPage.items.length);
        }
    }

    public void setAdapterCurrentPlayingAudioDuration(int duration){
        soundBoardAdapter.setCurrentlyPlayingAudioDuration(duration);
    }

    public void setAdapterSoundStoppedPlaying() {
        soundBoardAdapter.soundStoppedPlaying(null);
    }


}
