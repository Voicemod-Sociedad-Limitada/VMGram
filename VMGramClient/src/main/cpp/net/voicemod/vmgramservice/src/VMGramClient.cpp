/*
 * This is the source code of VMGramClient
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Voicemod S.L, 2023.
 */

#include "VMGramClient.h"

#include <aidl/net/voicemod/vmgramservice/VMGramAidl.h>
using aidl::net::voicemod::vmgramservice::IVMGramAidl;

#include <android/binder_auto_utils.h>
#include <android/binder_ibinder_jni.h>
using ndk::ScopedAStatus;

#include "JNIStringHelper.hpp"

#include <android/log.h>
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "VMGram:NDKClient", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "VMGram:NDKClient", __VA_ARGS__)

#include <algorithm>
#include <execution>
#include <string>

namespace {

    using namespace aidl::net::voicemod::vmgramservice;

    std::shared_ptr<IVMGramAidl> vmGramServiceAidl { nullptr };
    std::vector<float> monoBufferCopy;

    void initNative([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jclass clazz, [[maybe_unused]] jobject binder) {
        // TODO: Remove this method
    }

    void onServiceConnectedNative(JNIEnv *env, [[maybe_unused]] jobject thiz, jobject binder) {
        //std::lock_guard<std::mutex> guard(vmGramServiceMutex);
        AIBinder* pBinder = AIBinder_fromJavaBinder(env, binder);
        const ::ndk::SpAIBinder spAiBinder(pBinder);
        vmGramServiceAidl = IVMGramAidl::fromBinder(spAiBinder);
        //globalBinder = env->NewGlobalRef(binder);
        // TODO: If (vmGramServiceAidl == nullptr) verbose critical error
    }

    void onServiceDisconnectedNative([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz) {
        //std::lock_guard<std::mutex> guard(vmGramServiceMutex);
        vmGramServiceAidl = nullptr;
        // TODO: Connected callback ?
    }

    std::string nativeVoiceVorVoIPStart;
    void setNativeVoice(JNIEnv *env, jclass clazz, jstring voice_id) {
        nativeVoiceVorVoIPStart = voice_id != nullptr ? toSTDStringAndDeleteLocalRef(env, voice_id) : "";
    }

    template <typename Func>
    inline __attribute__((always_inline))
    void runServerFunctionOrCrash(Func f, const char * crashSource) {

        if(vmGramServiceAidl == nullptr) {
            LOGE("%s :: vmGramServiceAidl is NULL", crashSource);
            abort();
        }

        VmgsResultAidl vmgsResultAidl {0};
        ndk::ScopedAStatus status {
            f(&vmgsResultAidl)
        };

        if(!status.isOk()) {
            LOGE("%s :: AidlScopedAStatus is not OK", crashSource);
            abort();
        }

        if(vmgsResultAidl.value != 0) {
            LOGE("%s :: VmgsResultAidl is not 0", crashSource);
            abort();
        }

    }

} // namespace

extern "C" jint JNI_OnLoad(JavaVM* vm, [[maybe_unused]] void* reserved){

    JNIEnv *env;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("ERROR: GetEnv failed");
        abort();
    }

    //auto c = env ->FindClass( "net/voicemod/vmgramservice/VMGramClient" );
    //auto init { env -> GetStaticMethodID( c, "initNative", "(Lnet/voicemod/vmgramservice/VMGramClient;)V")};
    //auto onServiceConnected { env ->GetMethodID( c, "onServiceConnectedNative", "(Landroid/os/IBinder;)V" )};
    //auto onServiceDisconnected {env ->GetMethodID( c, "onServiceDisconnectedNative", "()V" )};
    //auto isVoiceLoaded { env->GetStaticMethodID(c, "setNativeVoice", "(Ljava/lang/String;)V")};

    jclass clazz { env->FindClass("net/voicemod/vmgramservice/VMGramClient") };

    JNINativeMethod methods[] = {
        {
            "initNative", "(Lnet/voicemod/vmgramservice/VMGramClient;)V",
            reinterpret_cast<void*>(&initNative)
        },{
            "onServiceConnectedNative", "(Landroid/os/IBinder;)V",
            reinterpret_cast<void*>(&onServiceConnectedNative)
        },{
            "onServiceDisconnectedNative", "()V",
            reinterpret_cast<void*>(&onServiceDisconnectedNative)
        },{
            "setNativeVoice", "(Ljava/lang/String;)V",
            reinterpret_cast<void*>(&setNativeVoice)
        }
    };

    env->RegisterNatives( clazz, methods, 4 );

    return JNI_VERSION_1_6;

}

#undef LOGE

using namespace aidl::net::voicemod::vmgramservice;

__attribute__((visibility("default")))
bool vmgsProcessBufferShort(void *shortAudioData, int numSamples) {

    constexpr const float kFactor = 1.f / 32768.f;

    short *shortAudioDataShortPtr { static_cast<short *>(shortAudioData) };

    if(vmGramServiceAidl == nullptr) {
        return false;
    }

    if(monoBufferCopy.size() < numSamples){
        monoBufferCopy.resize(numSamples);
    }

    for(size_t i=0; i<numSamples; i++) {
        monoBufferCopy[i] = shortAudioDataShortPtr[i] * kFactor;
    }

    auto scopedStatus { vmGramServiceAidl->processBufferNative(&monoBufferCopy, static_cast<int32_t>(numSamples)) };

    if(!scopedStatus.isOk())
        return false;

    for(size_t i=0; i<numSamples; i++) {
        shortAudioDataShortPtr[i] = static_cast<short>(std::clamp( monoBufferCopy[i] * 32768, -32768.f, 32767.f ));
    }

    return true;
}

__attribute__((visibility("default")))
void vmgsSetBypass(bool enable) {
    runServerFunctionOrCrash([enable](VmgsResultAidl * vmgsResultAidl)->::ndk::ScopedAStatus{
        return vmGramServiceAidl -> setBypass(enable, vmgsResultAidl);
    }, "vmgsSetBypass");
}

__attribute__((visibility("default")))
void vmgsEnableOfflineMode(bool enable) {
    runServerFunctionOrCrash([enable](VmgsResultAidl * vmgsResultAidl)->::ndk::ScopedAStatus{
        return vmGramServiceAidl -> enableOfflineMode(enable, vmgsResultAidl);
    }, "vmgsEnableOfflineMode");
}

__attribute__((visibility("default")))
void vmgsSetSampleRate(int sampleRate) {
    runServerFunctionOrCrash([sampleRate](VmgsResultAidl * vmgsResultAidl)->::ndk::ScopedAStatus{
        return vmGramServiceAidl -> setSampleRate(sampleRate, vmgsResultAidl);
    }, "vmgsSetSampleRate");
}

__attribute__((visibility("default")))
void vmgsLoadNativeVoiceOrEnableBypass() {
    if(nativeVoiceVorVoIPStart.empty()) {
        runServerFunctionOrCrash([](VmgsResultAidl * vmgsResultAidl)->::ndk::ScopedAStatus{
            return vmGramServiceAidl -> setBypass(true, vmgsResultAidl);
        }, "vmgsLoadNativeVoiceOrEnableBypass::setBypass(true)");
    } else {
        runServerFunctionOrCrash([](VmgsResultAidl * vmgsResultAidl)->::ndk::ScopedAStatus{
            return vmGramServiceAidl -> setBypass(false, vmgsResultAidl);
        }, "vmgsLoadNativeVoiceOrEnableBypass::setBypass(false)");
        runServerFunctionOrCrash([](VmgsResultAidl * vmgsResultAidl)->::ndk::ScopedAStatus{
            return vmGramServiceAidl -> loadVoice(nativeVoiceVorVoIPStart, vmgsResultAidl);
        }, "vmgsLoadNativeVoiceOrEnableBypass::loadVoice");
    }
}

__attribute__((visibility("default")))
void vmgsEnableBackgroundSounds(bool enable) {
    runServerFunctionOrCrash([enable](VmgsResultAidl * vmgsResultAidl)->::ndk::ScopedAStatus{
        return vmGramServiceAidl -> enableBackgroundSounds(enable, vmgsResultAidl);
    }, "vmgsLoadNativeVoiceOrEnableBypass");
}
