package org.telegram.vm.tracking;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mparticle.MPEvent;
import com.mparticle.MParticle;

import org.telegram.messenger.ApplicationLoader;

import java.util.Map;

public class TrackingHelper {

    private static final String voicemodSystemKey = "voicemod_system";
    private static final String voicemodSystemValue = "vm-gram";
    private static final String osKey = "os";
    private static final String osValue = "android";
    private static final String deviceKey = "device";
    private static final String appVersionKey = "app_version";
    private static final String deviceIdKey = "device_id";


    public static void trackEvent(TrackingEvent event) {
        MParticle mpInstance = MParticle.getInstance();
        if (mpInstance == null) return;
        addBaseProperties(event.properties);
        MPEvent mpEvent = new MPEvent.Builder(event.eventType, MParticle.EventType.Transaction)
                .customAttributes(event.properties)
                .build();

        mpInstance.logEvent(mpEvent);
        mpInstance.upload();
    }

    private static void addBaseProperties(Map<String, Object> properties) {
        checkAndAdd(properties, voicemodSystemKey, voicemodSystemValue);
        checkAndAdd(properties, osKey, osValue);
        checkAndAdd(properties, deviceKey, Build.MODEL);
        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            properties.put(appVersionKey, pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        properties.put(deviceIdKey, Build.ID);
    }

    private static void checkAndAdd(Map<String, Object> properties, String key, String value) {
        if (!properties.containsKey(key)) {
            properties.put(key, value);
        }
    }
}
