package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class BottomPagesView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress;
    private int scrollPosition;
    private int currentPage;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private RectF rect = new RectF();
    private float animatedProgress;
    private ViewPager viewPager;
    private int pagesCount;

    private int colorKey = -1;
    private int selectedColorKey = -1;

    private int selectedColorWh = -1;
    private int notSelectedColorWh = -1;

    private int selectedColorBl = -1;
    private int notSelectedColorBl = -1;

    public BottomPagesView(Context context, ViewPager pager, int count) {
        super(context);
        viewPager = pager;
        pagesCount = count;
    }

    public void setPageOffset(int position, float offset) {
        progress = offset;
        scrollPosition = position;
        invalidate();
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        invalidate();
    }

    public void setColor(int key, int selectedKey) {
        colorKey = key;
        selectedColorKey = selectedKey;
    }

    public void setDotColor(int selectedColorWh, int selectedColorBl, int notSelectedColorWh, int notSelectedColorBl) {
        this.selectedColorWh = selectedColorWh;
        this.selectedColorBl = selectedColorBl;
        this.notSelectedColorWh = notSelectedColorWh;
        this.notSelectedColorBl = notSelectedColorBl;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float d = AndroidUtilities.dp(5);
        if (colorKey >= 0) {
            paint.setColor((Theme.getColor(colorKey) & 0x00ffffff) | 0xb4000000);
        } else {
            paint.setColor(Theme.getCurrentTheme().isDark() ? 0xff555555 : 0xffbbbbbb);
        }
        if (notSelectedColorWh >= 0 && notSelectedColorBl >= 0) {
            paint.setColor(ContextCompat.getColor(getContext(), Theme.getCurrentTheme().isDark() ? notSelectedColorBl : notSelectedColorWh));
        }
        int x;
        currentPage = viewPager.getCurrentItem();
        for (int a = 0; a < pagesCount; a++) {
            if (a == currentPage) {
                continue;
            }
            x = a * AndroidUtilities.dp(11);
            rect.set(x, 0, x + AndroidUtilities.dp(5), AndroidUtilities.dp(5));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(2.5f), AndroidUtilities.dp(2.5f), paint);
        }
        if (selectedColorKey >= 0) {
            paint.setColor(Theme.getColor(selectedColorKey));
        } else if (selectedColorWh >= 0 && selectedColorBl >= 0) {
            paint.setColor(ContextCompat.getColor(getContext(), Theme.getCurrentTheme().isDark() ? selectedColorBl : selectedColorWh));
        } else {
            paint.setColor(0xff2ca5e0);
        }
        x = currentPage * AndroidUtilities.dp(11);
        if (progress != 0) {
            if (scrollPosition >= currentPage) {
                rect.set(x, 0, x + AndroidUtilities.dp(5) + AndroidUtilities.dp(11) * progress, AndroidUtilities.dp(5));
            } else {
                rect.set(x - AndroidUtilities.dp(11) * (1.0f - progress), 0, x + AndroidUtilities.dp(5), AndroidUtilities.dp(5));
            }
        } else {
            rect.set(x, 0, x + AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        }
        canvas.drawRoundRect(rect, AndroidUtilities.dp(2.5f), AndroidUtilities.dp(2.5f), paint);
    }
}
