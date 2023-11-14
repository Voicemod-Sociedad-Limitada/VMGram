package org.telegram.vm.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import java.util.function.Function;

public class AnimationUtils {

    public static Animation fadeInAnimation(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(duration);
        view.setAnimation(fadeIn);
        fadeIn.start();

        return fadeIn;
    }

    public static Animation fadeInAnimation(View view, int duration, Function onFinished) {
        return fadeInAnimation(view, duration, 0f, 1f, onFinished);
    }

    public static Animation fadeInAnimation(View view, int duration, float fromAlpha, float toAlpha, Function onFinished) {
        view.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(fromAlpha, toAlpha);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(duration);
        fadeIn.setFillAfter(true);
        view.setAnimation(fadeIn);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onFinished.apply(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        fadeIn.start();

        return fadeIn;
    }

    public static Animation fadeOutAnimation(View view, int duration) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(duration);
        view.setAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOut.start();

        return fadeOut;
    }
}