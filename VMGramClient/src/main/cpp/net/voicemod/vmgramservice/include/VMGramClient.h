/*
 * This is the source code of VMGramClientConnector
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Voicemod S.L, 2023.
 */

#ifndef VOICEMOD_VMGRAMCLIENT_H_INCLUDE
#define VOICEMOD_VMGRAMCLIENT_H_INCLUDE

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

bool vmgsProcessBufferShort(void *buffer, int numFrames);
void vmgsSetBypass(bool enable);
void vmgsEnableOfflineMode(bool enable);
void vmgsSetSampleRate(int sampleRate);
void vmgsLoadNativeVoiceOrEnableBypass();
void vmgsEnableBackgroundSounds(bool enable);

#ifdef __cplusplus
}
#endif

#endif //VOICEMOD_VMGRAMCLIENT_H_INCLUDE
