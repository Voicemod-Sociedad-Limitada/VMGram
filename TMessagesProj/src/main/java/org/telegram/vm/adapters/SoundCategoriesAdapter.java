package org.telegram.vm.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.vm.adapters.models.UISoundCategory;

import java.util.ArrayList;

public class SoundCategoriesAdapter extends BaseAdapter {
    private final Spinner spinner;
    private final Context context;
    private final ArrayList<UISoundCategory> categories;

    public SoundCategoriesAdapter(@NonNull Context context, @NonNull ArrayList<UISoundCategory> categories, Spinner spinner) {
        this.categories = categories;
        this.context = context;
        this.spinner = spinner;

    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getDropdownView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getSpinnerView(position, convertView, parent);
    }

    private View getSpinnerView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_category_spinner_item, parent, false);
        final TextView nameTv = (TextView) row.findViewById(R.id.category_name_tv);
        final ImageView arrowImg = (ImageView) row.findViewById(R.id.spinner_arrow_img);

        if (categories.get(position).name.equals(LocaleController.getString("DefaultSpinnerSoundCategory", R.string.DefaultSpinnerSoundCategory))) {
            nameTv.setTextColor(ContextCompat.getColor(context, R.color.vm_grey_manatee));
        } else {
            nameTv.setTextColor(ContextCompat.getColor(context, R.color.vm_grey_payne));
        }
        nameTv.setText(getCapitalizedCategoryName(categories.get(position)));
        arrowImg.setVisibility(View.VISIBLE);
        arrowImg.setColorFilter(ContextCompat.getColor(context, R.color.vm_grey_manatee));
        arrowImg.setRotation(180);
        return row;
    }

    private View getDropdownView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_category_spinner_item, parent, false);
        final TextView nameTv = (TextView) row.findViewById(R.id.category_name_tv);
        final ImageView arrowImg = (ImageView) row.findViewById(R.id.spinner_arrow_img);

        boolean isItemSelected = spinner.getSelectedItemPosition() == position;
        boolean isSelectCategoryItemShown = categories.get(position).name.equals(LocaleController.getString("DefaultSpinnerSoundCategory", R.string.DefaultSpinnerSoundCategory));
        if (isSelectCategoryItemShown) {
            isItemSelected = false;
        }
        row.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        arrowImg.setVisibility(View.INVISIBLE);
        if (position == 0) {
            if (isItemSelected) {
                arrowImg.setVisibility(View.VISIBLE);
                arrowImg.setColorFilter(ContextCompat.getColor(context, R.color.vm_white));
                row.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_first_item_spinner));
            } else {
                arrowImg.setColorFilter(ContextCompat.getColor(context, R.color.vm_grey_manatee));
            }
        } else if (position == categories.size() - 1) {
            if (isItemSelected) {
                arrowImg.setVisibility(View.VISIBLE);
                arrowImg.setColorFilter(ContextCompat.getColor(context, R.color.vm_white));
                row.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_last_item_spinner));
            }
        } else {
            if(isItemSelected){
                arrowImg.setVisibility(View.VISIBLE);
                arrowImg.setColorFilter(ContextCompat.getColor(context, R.color.vm_white));
                row.setBackgroundColor(ContextCompat.getColor(context, R.color.vm_grey_mineshaft));
            }
        }

        if (isItemSelected) {
            row.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.vm_grey_mineshaft)));
            nameTv.setTextColor(ContextCompat.getColor(context, R.color.vm_white));
        } else {
            row.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.vm_light_grey)));
            nameTv.setTextColor(ContextCompat.getColor(context, R.color.vm_grey_payne));
        }

        //Capitalize the first letter
        nameTv.setText(getCapitalizedCategoryName(categories.get(position)));
        if (isSelectCategoryItemShown) {
            //This will be true until the user changes the category
            nameTv.setText(LocaleController.getString("VoicemodSelectionSoundsCategory", R.string.VoicemodSelectionSoundsCategory));
            arrowImg.setVisibility(View.VISIBLE);
        }

        return row;
    }

    private String getCapitalizedCategoryName(UISoundCategory soundCategory) {
        return soundCategory.name.substring(0, 1).toUpperCase() + soundCategory.name.substring(1).toLowerCase();
    }
}
