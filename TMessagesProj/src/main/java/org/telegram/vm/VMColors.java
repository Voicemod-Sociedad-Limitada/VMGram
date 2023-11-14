package org.telegram.vm;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class VMColors {

    public static int getColor(Context context, int colorResource) {
        if (Theme.isCurrentThemeDay()) {
            return ContextCompat.getColor(context, colorResource);
        } else {
            return getDarkColorFromVMColor(colorResource);
        }
    }

    private static int getDarkColorFromVMColor(int colorResource){
        if (colorResource == R.color.vm_dark_grey) {
            return Color.parseColor("#384B5A");
        } else if (colorResource == R.color.vm_white) {
            return Color.parseColor("#212D3B");
        } else if (colorResource == R.color.vm_black) {
            return Color.parseColor("#FFFFFF");
        } else if (colorResource == R.color.vm_grey) {
            return Color.parseColor("#FFFFFF");
        } else if (colorResource == R.color.vm_grey_athens) {
            return Color.parseColor("#384B5A");
        } else if (colorResource == R.color.vm_blue) {
            return Color.parseColor("#00FFF6");
        } else if (colorResource == R.color.vm_light_grey) {
            return Color.parseColor("#384B5A");
        } else if (colorResource == R.color.vm_black_secondary) {
            return Color.parseColor("#97A0A7");
        }
        return -1;
    }
}
