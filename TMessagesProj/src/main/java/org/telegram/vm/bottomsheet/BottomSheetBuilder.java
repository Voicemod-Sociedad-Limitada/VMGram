package org.telegram.vm.bottomsheet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.vm.SoundsListHelper;
import org.telegram.vm.VMColors;
import org.telegram.vm.adapters.VoiceApplyClickListener;
import org.telegram.vm.adapters.VoiceApplyResultListener;
import org.telegram.vm.adapters.VoicePreviewClickListener;
import org.telegram.vm.adapters.VoicesListAdapter;
import org.telegram.vm.utils.KeyboardUtils;
import org.telegram.vm.utils.SizeUtils;

import java.util.function.Function;

public class BottomSheetBuilder {

    private final int RECYCLER_VIEW_BASE_HEIGHT_DP = 550;

    /***
     * Builds the bottom sheet with the list of voices.
     * @param context
     * @param config
     * @param onApplyListener - listener for the apply button click
     * @param onPreviewListener - listener for the preview button click
     * @return
     */
    public VMBottomSheet buildVoicesBottomSheet(Context context, BottomSheetConfig config, VoiceApplyClickListener onApplyListener, VoicePreviewClickListener onPreviewListener, Function<Void,Void> onDismissed){
        VMBottomSheet bs = new VMBottomSheet(context, true);

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.voices_list_bottom_sheet,null);

        TextView headerView = bottomSheetView.findViewById(R.id.headerLabel);
        headerView.setTextColor(VMColors.getColor(context, R.color.vm_black));

        bottomSheetView.findViewById(R.id.linear_parent).setBackgroundColor(VMColors.getColor(context, R.color.vm_white));

        RecyclerView rView = bottomSheetView.findViewById(R.id.voicesRecyclerView);
        rView.setLayoutManager(new LinearLayoutManager(context));
        setRecyclerViewHeight(rView,SizeUtils.dpToPixels(context, RECYCLER_VIEW_BASE_HEIGHT_DP));

        ImageView closeButton = bottomSheetView.findViewById(R.id.voicesListBottomSheetCloseButton);
        closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        closeButton.setOnClickListener(v -> {
            bs.dismiss();
            onDismissed.apply(null);
        });
        bs.setOnHideListener((bottomSheet) -> {
            onDismissed.apply(null);
        });

        VoicesListAdapter voicesListAdapter = new VoicesListAdapter(
            (voice, result) -> {
                VoiceApplyResultListener bottomSheetResult = (isApplied, shouldDismissed) -> {
                    result.onResultApplying(isApplied, shouldDismissed);
                    bottomSheetView.postDelayed(() -> {
                        if (shouldDismissed) {
                            bs.dismiss();
                            onDismissed.apply(null);
                        }
                    }, 500);
                };
                onApplyListener.onApplyVoiceClick(voice, bottomSheetResult);
            },
            onPreviewListener);

        voicesListAdapter.showPreviewButtons(config.previewVoiceVisible);
        voicesListAdapter.showAppliedVoice(config.showAppliedVoice);

        EditText searchEditText = bottomSheetView.findViewById(R.id.searchEditText);
        searchEditText.setBackgroundTintList(ColorStateList.valueOf(VMColors.getColor(context, R.color.vm_grey_athens)));

        searchEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length() > 0){
                            voicesListAdapter.setSearchTerm(s.toString());
                        }else{
                            voicesListAdapter.setSearchTerm(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        rView.setAdapter(voicesListAdapter);

        bs.setCustomView(bottomSheetView);
        bs.setAllowNestedScroll(true);
        bs.setCanDismissWithSwipe(false);
        bs.setApplyBottomPadding(false);

        KeyboardUtils.setKeyboardVisibilityListener((keyboardVisible, keyboardHeight) -> {
            setRecyclerViewHeight(rView,(keyboardVisible) ? SizeUtils.dpToPixels(context, RECYCLER_VIEW_BASE_HEIGHT_DP) - keyboardHeight : SizeUtils.dpToPixels(context, RECYCLER_VIEW_BASE_HEIGHT_DP));
        });

        bs.setOnDismissListener(dialogInterface -> {
            KeyboardUtils.setKeyboardVisibilityListener(null);
        });

        // This is required to avoid layout problems.
        // VMBottomSheet recalculates its size with a custom method (onLayout), and the resulting recyclerview inside it has
        // wrong width if the data is set from the very beginning. This listener is called after the layout is calculated
        // so that the items width are correct after notifyDataSetChanged() (and subsequently requestLayout()) is called.
        bs.setLayoutListener(() -> {
                voicesListAdapter.setVoicesAndCategories(config.voicesCategories, config.voices);
            }
        );

        return bs;
    }

    private void setRecyclerViewHeight(RecyclerView rView, int height){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rView.getLayoutParams();

        params.height = height;

        rView.setLayoutParams(params);
    }


    public VMBottomSheet buildSoundsBottomSheet(Context context, SoundsListHelper soundsListHelper, Function<Void,Void> onDismissed) {
        VMBottomSheet bottomSheet = new VMBottomSheet(context, true);

        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sound_list_bottom_sheet,null);
        View soundsListView = soundsListHelper.getSoundboardView(true);
        LinearLayout linearLayout = bottomSheetView.findViewById(R.id.linear_parent);
        linearLayout.addView(soundsListView);

        ImageView closeButton = bottomSheetView.findViewById(R.id.soundListBottomSheetCloseButton);
        closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        closeButton.setOnClickListener(v -> {
            onDismissed.apply(null);
            bottomSheet.dismiss();
        });

        bottomSheet.setCustomView(bottomSheetView);
        bottomSheet.setAllowNestedScroll(true);
        bottomSheet.setCanDismissWithSwipe(false);
        bottomSheet.setApplyBottomPadding(false);
        bottomSheet.setOnDismissListener(dialogInterface -> {
            KeyboardUtils.setKeyboardVisibilityListener(null);
        });
        bottomSheet.setOnHideListener((bs) -> {
            onDismissed.apply(null);
        });

        bottomSheet.setLayoutListener(() -> {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) soundsListView.getLayoutParams();
                    params.height = SizeUtils.dpToPixels(context, 400);
                    soundsListView.setLayoutParams(params);
                }
        );
        return bottomSheet;
    }
}
