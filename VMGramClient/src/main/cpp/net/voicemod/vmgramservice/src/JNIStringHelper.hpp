/*
 * This is the source code of VMGramClient
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Voicemod S.L, 2023.
 */

#ifndef VOICEMOD_JNISTRINGHELPER_H_INCLUDE
#define VOICEMOD_JNISTRINGHELPER_H_INCLUDE

#include <jni.h>

template<bool ReleaseJNIString>
__attribute__((always_inline)) inline
std::string toSTDString(__attribute__((nonnull)) JNIEnv * env, __attribute__((nonnull)) jstring jniS) {

    auto cS { env -> GetStringUTFChars(jniS, nullptr) };

    std::string cppS { cS };

    env ->ReleaseStringUTFChars(jniS, cS);

    if constexpr (ReleaseJNIString) {
        // TODO: Warning can be global ref, so ensure type & call appropriate release
        env -> DeleteLocalRef(jniS);
    }

    return cppS;

}

__attribute__((always_inline)) inline
std::string toSTDStringAndDeleteLocalRef(__attribute__((nonnull)) JNIEnv * env, __attribute__((nonnull)) jstring jniS) {
        return toSTDString<true>(env, jniS);
}

__attribute__((always_inline)) inline
std::string toSTDStringNoDeleteLocalRef(__attribute__((nonnull)) JNIEnv * env, __attribute__((nonnull)) jstring jniS) {
        return toSTDString<false>(env, jniS);
}

__attribute__((always_inline)) inline
std::string toSTDStringAndDeleteLocalRef(__attribute__((nonnull)) JNIEnv * env, __attribute__((nonnull)) jobject jniS) {
        return toSTDString<true>(env, static_cast<jstring>(jniS));
}

__attribute__((always_inline)) inline
std::string toSTDStringNoDeleteLocalRef(__attribute__((nonnull)) JNIEnv * env, __attribute__((nonnull)) jobject jniS) {
        return toSTDString<false>(env, static_cast<jstring>(jniS));
}

#endif //VOICEMOD_JNISTRINGHELPER_H_INCLUDE
