package org.telegram.vm.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.vm.VMColors;
import org.telegram.vm.VMGramLiveData;
import org.telegram.vm.adapters.models.UIVoice;
import org.telegram.vm.adapters.models.UIVoiceCategory;
import org.telegram.vm.adapters.models.VoiceAdapterDataItem;
import org.telegram.vm.dto.VMVoice;
import org.telegram.vm.models.Voice;
import org.telegram.vm.models.VoiceCategory;
import org.telegram.vm.utils.SoundUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VoicesListAdapter extends RecyclerView.Adapter<VoicesListAdapter.ItemsViewHolder> implements VoiceApplyResultListener {

    private static final String TAG = "VoicesListAdapter";
    private final int ITEM_VIEW_TYPE_HEADER = 0;
    private final int ITEM_VIEW_TYPE_ITEM = 1;

    private VoiceAdapterDataItem[] originalDataSet, currentDataSet;
    private VoiceCategory[] previousVoiceCategories;
    private Voice[] voices;
    private final VoiceApplyClickListener applyClickListener;
    private final VoicePreviewClickListener previewClickListener;
    private boolean showPreviewButtons;
    private boolean showAppliedVoice;
    private boolean currentlyInApplying;

    @Override
    public void onResultApplying(boolean isApplied, boolean shouldDismissed) {
        if (isApplied) {
            showAppliedVoice = true;
            notifyDataSetChanged();
        }
        currentlyInApplying = false;
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder {
        protected final Resources resources;

        public ItemsViewHolder(View view, Resources resources) {
            super(view);
            this.resources = resources;
        }
    }

    public class HeaderViewHolder extends ItemsViewHolder {

        TextView headerTextView;

        public HeaderViewHolder(View view, Resources resources) {
            super(view, resources);

            headerTextView = view.findViewById(R.id.headerText);
        }

        TextView getHeaderTextView() {
            return headerTextView;
        }

    }

    public class VoiceViewHolder extends ItemsViewHolder implements View.OnClickListener {
        private final TextView voiceName;
        private final ImageView imageIcon;
        private final ProgressBar previewProgressBar;
        private final ProgressBar applyProgressBar;
        private final ImageView previewButton;
        private final ImageView voiceIconBackground, previewVoiceIcon;
        private final TextView appliedVoiceTv;
        private final Button applyButton;
        private boolean isAnimatingProgressBar = false;

        private final VoicesListAdapter adapter;

        private LocalBroadcastManager localBroadcastManager;
        private final BroadcastReceiver conversionCancelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("VM", "onReceive - " + voiceName.getText());
                if(isAnimatingProgressBar || previewProgressBar.isIndeterminate()) {
                    previewProgressBar.setProgress(0);
                    previewProgressBar.setVisibility(View.INVISIBLE);
                    previewProgressBar.clearAnimation();
                }
            }
        };

        public VoiceViewHolder(View view, Resources resources, VoicesListAdapter adapter) {
            super(view, resources);
            // Define click listener for the ViewHolder's View

            this.adapter = adapter;

            applyButton = view.findViewById(R.id.applyVoiceBtn);
            previewButton = view.findViewById(R.id.previewVoiceBtn);
            previewButton.setVisibility(showPreviewButtons ? View.VISIBLE : View.GONE);
            previewVoiceIcon = view.findViewById(R.id.previewVoiceIcon);
            previewVoiceIcon.setVisibility(showPreviewButtons ? View.VISIBLE : View.GONE);

            voiceName = view.findViewById(R.id.voiceName);
            imageIcon = view.findViewById(R.id.voiceIcon);

            voiceIconBackground = view.findViewById(R.id.voiceIconBackground);

            appliedVoiceTv = view.findViewById(R.id.appliedTv);

            applyButton.setOnClickListener(this);
            previewButton.setOnClickListener(this);

            previewProgressBar = view.findViewById(R.id.previewVoiceProgress);
            previewProgressBar.setIndeterminate(true);
            applyProgressBar = view.findViewById(R.id.applyVoiceProgress);

            //We get an event when a conversion is cancelled
            //If the conversion has been cancelled
            localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
            localBroadcastManager.registerReceiver(conversionCancelReceiver, new IntentFilter(SoundUtils.SOUND_PREVIEW_CANCELLED_ACTION));
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            localBroadcastManager.unregisterReceiver(conversionCancelReceiver);
        }

        public TextView getVoiceNameTextField() {
            return voiceName;
        }

        public ImageView getVoiceIconImageView() {
            return imageIcon;
        }

        public void showAppliedVoiceTv(boolean show) {
            appliedVoiceTv.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            applyButton.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            applyProgressBar.postDelayed(() -> {
                applyProgressBar.setVisibility(View.GONE);
            },50);
        }

        public void showApplyingProgress(){
            appliedVoiceTv.setVisibility(View.INVISIBLE);
            applyButton.setText("");
            // It's made for more smooth animation
            applyProgressBar.postDelayed(() -> {
                applyProgressBar.setVisibility(View.VISIBLE);
            },50);
        }

        public void reset() {
            previewProgressBar.setProgress(0);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.applyVoiceBtn) {
                if (adapter.currentlyInApplying) {
                    Toast.makeText(view.getContext(), LocaleController.getString("AccDescrVMVoiceIsApplying", R.string.VoiceIsApplying), Toast.LENGTH_SHORT).show();
                } else {
                    adapter.currentlyInApplying = true;
                    showApplyingProgress();
                    adapter.onApplyVoiceClicked(getAdapterPosition());
                    //if (showAppliedVoice) notifyDataSetChanged();
                }
            } else if (view.getId() == R.id.previewVoiceBtn) {
                adapter.onPreviewVoiceClicked(getAdapterPosition());
                UIVoice previewVoice = (UIVoice) currentDataSet[getAdapterPosition()];
                SoundUtils.convertAndPlayPreviewSound(
                    previewVoice.id,
                    () -> {
                        previewProgressBar.setVisibility(View.VISIBLE);
                    },
                    () -> {
                        previewProgressBar.setProgress(0);
                        previewProgressBar.setVisibility(View.INVISIBLE);
                    }
                );
            }
        }
    }

    public VoicesListAdapter(VoiceApplyClickListener applyClickListener, VoicePreviewClickListener previewClickListener) {
        this.applyClickListener = applyClickListener;
        this.previewClickListener = previewClickListener;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ItemsViewHolder holder) {
        //This is to avoid an inconsistent state of an item which has an animation in progress.
        //If the item is detached from the window, the animation will be stopped, and if the holder
        //is not recycled, onBindViewHolder will not be called and the item view will not be reset.
        super.onViewAttachedToWindow(holder);
        if (holder instanceof VoiceViewHolder) {
            ((VoiceViewHolder) holder).reset();
        }
    }

    public void onApplyVoiceClicked(int position) {
        applyClickListener.onApplyVoiceClick((UIVoice) currentDataSet[position], this);
    }

    public void onPreviewVoiceClicked(int position) {
        previewClickListener.onPreviewVoiceClick((UIVoice) currentDataSet[position]);
    }

    public void setVoicesAndCategories(VoiceCategory[] voicesCategories, Voice[] voices) {
        if (Arrays.equals(this.previousVoiceCategories, voicesCategories)) {
            return;
        }

        this.voices = voices;
        previousVoiceCategories = voicesCategories;

        this.originalDataSet = processCategoriesAndVoices(voicesCategories);
        currentDataSet = originalDataSet;

        notifyDataSetChanged();
    }

    public void showPreviewButtons(boolean showPreviewButtons){
        this.showPreviewButtons = showPreviewButtons;
    }

    public void showAppliedVoice(boolean showAppliedVoice){
        this.showAppliedVoice = showAppliedVoice;
    }

    private VoiceAdapterDataItem[] processCategoriesAndVoices(VoiceCategory[] voicesCategories) {
        ArrayList<VoiceAdapterDataItem> data = new ArrayList<>();

        for (VoiceCategory voicesCategory : voicesCategories) {
            data.add(new UIVoiceCategory(voicesCategory.categoryKey));
            Voice[] voices = voicesCategory.voices;
            for (Voice voice : voices) {
                data.add(new UIVoice(voice.id, voice.name, voice.id));
            }
        }

        VoiceAdapterDataItem[] newData = new VoiceAdapterDataItem[data.size()];
        newData = data.toArray(newData);

        return newData;
    }

    public void setSearchTerm(String searchTerm) {
        if (searchTerm != null) {
            //While searching we only filter voices, since we don't want to show dupped voices and multiple categories with the same voices.
            List<UIVoice> uiVoices;

            Voice[] filteredVoices = Arrays.stream(voices).filter(voice -> voice.name.toLowerCase().contains(searchTerm.toLowerCase())).toArray(Voice[]::new);
            uiVoices = Arrays.stream(filteredVoices).map(voice -> new UIVoice(voice.id, voice.name, voice.id)).collect(Collectors.toList());

            UIVoice[] arrayFilteredVoices = new UIVoice[uiVoices.size()];
            arrayFilteredVoices = uiVoices.toArray(arrayFilteredVoices);

            currentDataSet = arrayFilteredVoices;
        } else {
            currentDataSet = originalDataSet;

        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (currentDataSet[position] instanceof UIVoiceCategory) {
            return ITEM_VIEW_TYPE_HEADER;
        } else if (currentDataSet[position] instanceof UIVoice) {
            return ITEM_VIEW_TYPE_ITEM;
        } else {
            throw new IllegalArgumentException("Unknown item type");
        }
    }


    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.voices_list_item, parent, false);
            return new VoiceViewHolder(view, parent.getResources(), this);
        } else if (viewType == ITEM_VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_list_item, parent, false);
            return new HeaderViewHolder(view, parent.getResources());
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        VoiceAdapterDataItem item = currentDataSet[position];

        if (holder instanceof HeaderViewHolder) { //Category
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            UIVoiceCategory category = (UIVoiceCategory) item;

            int headerTextId = headerViewHolder.resources.getIdentifier(category.headerTextKey, "string", holder.itemView.getContext().getPackageName());
            if (headerTextId != 0) {
                headerViewHolder.getHeaderTextView().setText(headerTextId);
            } else {
                headerViewHolder.getHeaderTextView().setText(category.headerTextKey);
            }
            headerViewHolder.getHeaderTextView().setTextColor(VMColors.getColor(headerViewHolder.getHeaderTextView().getContext(), R.color.vm_black_secondary));


        } else if (holder instanceof VoiceViewHolder) { //Voice
            VoiceViewHolder voiceViewHolder = (VoiceViewHolder) holder;
            voiceViewHolder.reset();

            UIVoice voice = (UIVoice) item;

            Context context = voiceViewHolder.itemView.getContext();

            voiceViewHolder.getVoiceNameTextField().setText(voice.name);
            voiceViewHolder.getVoiceNameTextField().setTextColor(VMColors.getColor(context, R.color.vm_grey));

            voiceViewHolder.previewProgressBar.getProgressDrawable().setTint(VMColors.getColor(context, R.color.vm_black));
            voiceViewHolder.previewProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(VMColors.getColor(context, R.color.vm_black)));
            voiceViewHolder.previewProgressBar.setIndeterminateTintList(ColorStateList.valueOf(VMColors.getColor(context, R.color.vm_black)));

            voiceViewHolder.previewButton.setBackgroundTintList(ColorStateList.valueOf(VMColors.getColor(context, R.color.vm_grey_athens)));

            voiceViewHolder.voiceIconBackground.setColorFilter(VMColors.getColor(context, R.color.vm_light_grey), PorterDuff.Mode.MULTIPLY);

            voiceViewHolder.appliedVoiceTv.setTextColor(ContextCompat.getColor(context, R.color.vm_black));

            if (showAppliedVoice) {
                VMVoice currentAppliedVoice = VMGramLiveData.getCallCurrentVoice();
                if (currentAppliedVoice != null && ((UIVoice) item).id.equalsIgnoreCase(currentAppliedVoice.id)) {
                    voiceViewHolder.showAppliedVoiceTv(true);
                } else {
                    voiceViewHolder.showAppliedVoiceTv(false);
                }
            } else {
                voiceViewHolder.showAppliedVoiceTv(false);
            }

            int imageId = voiceViewHolder.resources.getIdentifier("voiceicon_" + voice.getIconResourceKey(), "drawable", holder.itemView.getContext().getPackageName());
            voiceViewHolder.getVoiceIconImageView().setImageResource(imageId == 0 ? R.drawable.dino_pic : imageId);
        }
    }

    @Override
    public int getItemCount() {
        return currentDataSet != null ? currentDataSet.length : 0;
    }

}
