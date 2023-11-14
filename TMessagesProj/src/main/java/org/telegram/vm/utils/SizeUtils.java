package org.telegram.vm.utils;

import android.content.Context;

public class SizeUtils {

    public static int dpToPixels(Context context, int dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }

}
