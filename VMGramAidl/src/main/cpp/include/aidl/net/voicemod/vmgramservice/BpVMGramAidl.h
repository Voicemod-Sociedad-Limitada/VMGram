#pragma once

#include "aidl/net/voicemod/vmgramservice/VMGramAidl.h"

#include <android/binder_ibinder.h>

namespace aidl {
namespace net {
namespace voicemod {
namespace vmgramservice {
class BpVMGramAidl : public ::ndk::BpCInterface<IVMGramAidl> {
public:
  BpVMGramAidl(const ::ndk::SpAIBinder& binder);
  virtual ~BpVMGramAidl();

  ::ndk::ScopedAStatus getResultDescription(const ::aidl::net::voicemod::vmgramservice::VmgsResultAidl& in_result, std::string* _aidl_return) override;
  ::ndk::ScopedAStatus getVersionString(std::string* _aidl_return) override;
  ::ndk::ScopedAStatus initSDK(const std::string& in_clientKey, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus getVoiceNames(std::vector<std::string>* out_voiceNames, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus setSampleRate(int32_t in_sampleRate, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus loadVoice(const std::string& in_voice, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus enableBackgroundSounds(bool in_enable, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus playSoundboardSound(const std::string& in_id, const std::string& in_file, const std::string& in_Path, bool in_loop, bool in_muteOtherSounds, bool in_muteVoice, float in_volume, bool in_sendToVac, bool in_fade, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus stopAllSoundboardSounds(::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus setHearSoundboardSelf(bool in_enable, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus processBufferNative(std::vector<float>* in_monoBuffer, int32_t in_nFrames) override;
  ::ndk::ScopedAStatus convertAndPlay(const std::string& in_source, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus cancelConvertAndPlay(::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus enableOfflineMode(bool in_offlineMode, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
  ::ndk::ScopedAStatus setBypass(bool in_enable, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) override;
};
}  // namespace vmgramservice
}  // namespace voicemod
}  // namespace net
}  // namespace aidl
