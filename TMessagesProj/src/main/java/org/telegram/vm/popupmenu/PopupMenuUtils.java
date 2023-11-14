package org.telegram.vm.popupmenu;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import org.telegram.messenger.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/***
 * Helper class to build PopupMenus with icons.
 */
public class PopupMenuUtils {

    @NonNull
    public static PopupMenu getVoiceCallOptionsPopupMenu(Context context, View anchor){
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(R.menu.call_popup_menu, popup.getMenu());

        enableIcons(popup);

        return popup;
    }

    public static void changeFontSizePopUpMenuItems(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            int end = spanString.length();
            AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(17, true);
            spanString.setSpan(textSize, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(spanString);
        }
    }

    private static void enableIcons(PopupMenu popup){
        //This reflection code is used to enable icons without inheriting from PopupMenu.
        //The inheritance solution requires AppCompat and I don't want to add dependencies to
        //the project.
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
