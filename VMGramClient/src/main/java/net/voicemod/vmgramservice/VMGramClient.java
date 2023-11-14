/*
 * This is the source code of VMGramClientConnector
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Voicemod S.L, 2023.
 */

package net.voicemod.vmgramservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("JavaJniMissingFunction")
public class VMGramClient implements ServiceConnection {

    private static final String TAG = "VMGram:Client";

    // Listener of connection or disconnection of the service.
    // It will be used in the initialization of VMGramClient and in the activities that want to subscribe to
    // VMGramService connections or disconnections.
    public interface ConnectionDisconnectionListener {
        void onServiceConnected();
        void onServiceDisconnected();
    }

    // Initial connection listener
    private final ConnectionDisconnectionListener mInitListener;
    private boolean firstConnectionDone = false;

    // AIDL for the communication between VMGramClient and VMGramService
    private VMGramAidl mServiceAidl = null;

    // Mutually exclusive variables for connections and disconnections while calls to the server are being executed.
    private volatile boolean mIsServiceConnected = false;
    private final ConditionVariable mServiceConnectionWaitLock = new ConditionVariable();

    // Intent for VMGramClient to VMGramService bindings.
    // If null it means that the package does not have the proprietary code.
    Intent mVmGramServiceIntent;

    // Coupling to a Context. Remember to decouple from the context when it is not necessary.
    private Context mContext;

    // Singleton
    private static VMGramClient mInstance = null;
    public static VMGramClient getInstance() { return mInstance; }

    public static void init(@NonNull Context context, @NonNull ConnectionDisconnectionListener initListener) {
        if(mInstance == null) {
            System.loadLibrary("VMGramClient");
            mInstance = new VMGramClient(context, initListener);
        }
    }

    public static void terminate() {
        // TODO: Fill with the code
    }

    void tryBind(){
        try {
            Class<?> vmGramClass = Class.forName("net.voicemod.vmgramservice.VMGramService");
            mVmGramServiceIntent = new Intent(mContext, vmGramClass);
            mContext.bindService(mVmGramServiceIntent, this, Context.BIND_AUTO_CREATE);
        } catch (ClassNotFoundException e) {
            mVmGramServiceIntent = null;
        }
    }

    // TODO: Delete
    private native static void initNative(VMGramClient client);

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        Log.d(TAG, "onServiceConnected");

        mServiceAidl = VMGramAidl.Stub.asInterface(service);
        mIsServiceConnected = true;
        onServiceConnectedNative(service);
        mServiceConnectionWaitLock.open();

        if(!firstConnectionDone) {
            mInitListener.onServiceConnected();
            firstConnectionDone = true;
        }

    }
    private native void onServiceConnectedNative(IBinder binder);

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        onServiceDisconnectedNative();
        mInitListener.onServiceDisconnected();
    }
    private native void onServiceDisconnectedNative();

    private VMGramClient(Context context, ConnectionDisconnectionListener initListener) {
        mContext = context;
        mInitListener = initListener;
        tryBind();
    }

    //------------------------------------------------------------------------------------------------------------------

    // Static Sync methods with fatal error

    public static void setSampleRateServiceOrCrash(float sampleRate, String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.setSampleRate((int) sampleRate);
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.setSampleRate returned " + result.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.setSampleRate throws a RemoteException");
        }
    }

    public static void loadVoiceServiceOrCrash(String voiceToLoad, String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.loadVoice(voiceToLoad);
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.loadVoice returned " + result.toString());
            }
            //nativeLoadVoiceService(voiceToLoad);
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.loadVoice throws a RemoteException");
        }
    }

    public native static void setNativeVoice(String voiceID);

    public static void enableBackgroundSoundsServiceOrCrash(boolean enable, String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.enableBackgroundSounds(enable);
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.setEnableBackgroundSounds returned " + result.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.setEnableBackgroundSounds throws a RemoteException");
        }
    }

    public static void setHearSoundboardSelfOrCrash(boolean enable, String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.setHearSoundboardSelf(enable);
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.setHearSoundboardSelf returned " + result.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.setHearSoundboardSelf throws a RemoteException");
        }
    }

    public static void playSoundOrCrash(String id, String file, String path, boolean loop, boolean muteOtherSounds, boolean muteVoice, float volume, boolean sendToVac, boolean fade, String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.playSoundboardSound(id, file, path, loop, muteOtherSounds, muteVoice, volume, sendToVac, fade);
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.playSoundboardSound returned " + result.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.playSoundboardSound throws a RemoteException");
        }
    }

    public static void stopSoundsServiceOrCrash(String errorSource){
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.stopAllSoundboardSounds();
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.stopAllSoundboardSounds returned " + result.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.stopAllSoundboardSounds throws a RemoteException");
        }
    }

    public static String getVersionStringServiceOrCrash(String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            String version =  mInstance.mServiceAidl.getVersionString();
            return version;
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.getVersionString throws a RemoteException");
        }
    }

    public static void enableOfflineModeServiceOrCrash(boolean offlineMode, String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.enableOfflineMode(offlineMode);
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.enableOfflineMode returned " + result.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.enableOfflineMode throws a RemoteException");
        }
    }

    public static void setBypassServiceOrCrash(boolean enable, String errorSource) {
        while(!mInstance.mIsServiceConnected) { mInstance.mServiceConnectionWaitLock.block(); }
        try {
            VmgsResultAidl result = mInstance.mServiceAidl.setBypass(enable);
            if(result != VmgsResultAidl.vcmdNoError) {
                throw new RuntimeException(errorSource + " ; mServiceAidl.setBypass returned " + result.toString());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(errorSource + " ; mServiceAidl.enableOfflineMode throws a RemoteException");
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    // CompletableFuture

    public CompletableFuture<Pair<VmgsResultAidl,RemoteException>> initSDK(@NonNull String clientKey) {
        return CompletableFuture.supplyAsync(() -> {
            while(!mIsServiceConnected) { mServiceConnectionWaitLock.block(); }
            try {
                return new Pair<>(mServiceAidl.initSDK(clientKey), null);
            } catch (RemoteException e) {
                return new Pair<>(VmgsResultAidl.vcmdVMGramServiceException, e);
            }
        });
    }

    public CompletableFuture<Pair<VmgsResultAidl, RemoteException>> getVoiceNames(@NonNull List<String> voiceNames) {
        return CompletableFuture.supplyAsync(() -> {
            while(!mIsServiceConnected) { mServiceConnectionWaitLock.block(); }
            try {
                return new Pair<>(mServiceAidl.getVoiceNames(voiceNames), null);
            } catch (RemoteException e) {
                return new Pair<>(VmgsResultAidl.vcmdVMGramServiceException, e);
            }
        });
    }

    public CompletableFuture<Pair<VmgsResultAidl, RemoteException>> convertAndPlay(@NonNull String source) {
        return CompletableFuture.supplyAsync(() -> {
            while(!mIsServiceConnected) { mServiceConnectionWaitLock.block(); }
            try {
                return new Pair<>(mServiceAidl.convertAndPlay(source), null);
            } catch (RemoteException e) {
                return new Pair<>(VmgsResultAidl.vcmdVMGramServiceException, e);
            }
        });
    }

    public CompletableFuture<Pair<VmgsResultAidl, RemoteException>> cancelConvertAndPlay() {
        return CompletableFuture.supplyAsync(() -> {
            while(!mIsServiceConnected) { mServiceConnectionWaitLock.block(); }
            try {
                return new Pair<>(mServiceAidl.cancelConvertAndPlay(), null);
            } catch (RemoteException e) {
                return new Pair<>(VmgsResultAidl.vcmdVMGramServiceException, e);
            }
        });
    }
}

