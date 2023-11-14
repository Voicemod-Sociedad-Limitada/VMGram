package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import org.telegram.messenger.AndroidUtilities;

public class BetaSpan extends ReplacementSpan {

    private final int backgroundColor;
    private final int textColor;
    private final int paddingLeft;
    private final int paddingRight;
    private final int paddingTop;
    private final int paddingBottom;

    public BetaSpan(int backgroundColor, int textColor,
                    int paddingLeft,
                    int paddingRight,
                    int paddingTop,
                    int paddingBottom) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        return (int) (paddingLeft +
                paint.measureText(text.subSequence(start, end).toString()) +
                paddingRight);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, Paint paint) {
        float width = paint.measureText(text.subSequence(start, end).toString());
        RectF rect = new RectF(x, top - paddingTop
                - paint.getFontMetricsInt().top + paint.getFontMetricsInt().ascent
                , x + width + paddingLeft + paddingRight, bottom + paddingBottom);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rect, AndroidUtilities.dp(8), AndroidUtilities.dp(8), paint);
        paint.setColor(textColor);
        canvas.drawText(text, start, end, x + paddingLeft,
                y - paint.getFontMetricsInt().descent / 2, paint);
    }
}
