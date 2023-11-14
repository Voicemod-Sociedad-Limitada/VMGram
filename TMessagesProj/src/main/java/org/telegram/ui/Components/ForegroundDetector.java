/*
 * This is the source code of Telegram for Android v. 5.x.x
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.vm.VMUSDKStatsHelper;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

public class ForegroundDetector implements Application.ActivityLifecycleCallbacks {

    public interface Listener {
        void onBecameForeground();
        void onBecameBackground();
    }

    private int refs;
    private int aliveActivitiesCounter = 0;
    private boolean wasInBackground = true;
    private long enterBackgroundTime = 0;
    private CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();
    private WeakReference<Activity> lastActivity = new WeakReference<>(null);
    private static ForegroundDetector Instance = null;

    public static ForegroundDetector getInstance() {
        return Instance;
    }

    public ForegroundDetector(Application application) {
        Instance = this;
        application.registerActivityLifecycleCallbacks(this);
        listeners.add(new Listener() {
            @Override
            public void onBecameForeground() {
                VMUSDKStatsHelper.onAppForeground();
            }

            @Override
            public void onBecameBackground() {
                VMUSDKStatsHelper.onAppBackground();
            }
        });
    }

    public boolean isForeground() {
        return refs > 0;
    }

    public boolean isBackground() {
        return refs == 0;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++refs == 1) {
            if (SystemClock.elapsedRealtime() - enterBackgroundTime < 200) {
                wasInBackground = false;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("switch to foreground");
            }
            for (Listener listener : listeners) {
                try {
                    listener.onBecameForeground();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public boolean isWasInBackground(boolean reset) {
        if (reset && Build.VERSION.SDK_INT >= 21 && (SystemClock.elapsedRealtime() - enterBackgroundTime < 200)) {
            wasInBackground = false;
        }
        return wasInBackground;
    }

    public void resetBackgroundVar() {
        wasInBackground = false;
    }

    @Nullable
    public Activity getLastActivity() {
        return lastActivity.get();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (--refs == 0) {
            enterBackgroundTime = SystemClock.elapsedRealtime();
            wasInBackground = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("switch to background");
            }
            for (Listener listener : listeners) {
                try {
                    listener.onBecameBackground();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        aliveActivitiesCounter++;
        lastActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        lastActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        aliveActivitiesCounter--;
    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreDestroyed(activity);
        if (aliveActivitiesCounter == 1) {
            onAppFinishing();
        }
    }

    private void onAppFinishing() {
        VMUSDKStatsHelper.onAppDestroyed();
    }
}
