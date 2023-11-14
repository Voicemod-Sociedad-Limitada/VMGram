#pragma once

#include <android/binder_interface_utils.h>

#include <cstdint>
#include <memory>
#include <optional>
#include <string>
#include <vector>
#ifdef BINDER_STABILITY_SUPPORT
#include <android/binder_stability.h>
#endif  // BINDER_STABILITY_SUPPORT
#include "ParcelableBoolean.hpp"
#include "ParcelableInteger.hpp"
#include "ParcelableString.hpp"
#include "VmgsResultAidl.hpp"

namespace aidl {
namespace net {
namespace voicemod {
namespace vmgramservice {
class IVMGramAidl : public ::ndk::ICInterface {
public:
  static const char* descriptor;
  IVMGramAidl();
  //WARNING: MAINTAIN THE INLINE DESTRUCTOR DECLARATION
  virtual inline ~IVMGramAidl() = default;
  static std::shared_ptr<IVMGramAidl> fromBinder(const ::ndk::SpAIBinder& binder);
  static binder_status_t writeToParcel(AParcel* parcel, const std::shared_ptr<IVMGramAidl>& instance);
  static binder_status_t readFromParcel(const AParcel* parcel, std::shared_ptr<IVMGramAidl>* instance);
  static bool setDefaultImpl(std::shared_ptr<IVMGramAidl> impl);
  static const std::shared_ptr<IVMGramAidl>& getDefaultImpl();
  virtual ::ndk::ScopedAStatus getResultDescription(const ::aidl::net::voicemod::vmgramservice::VmgsResultAidl& in_result, std::string* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus getVersionString(std::string* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus initSDK(const std::string& in_clientKey, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus getVoiceNames(std::vector<std::string>* out_voiceNames, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus setSampleRate(int32_t in_sampleRate, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus loadVoice(const std::string& in_voice, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus enableBackgroundSounds(bool in_enable, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus playSoundboardSound(const std::string& in_id, const std::string& in_file, const std::string& in_Path, bool in_loop, bool in_muteOtherSounds, bool in_muteVoice, float in_volume, bool in_sendToVac, bool in_fade, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus stopAllSoundboardSounds(::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus setHearSoundboardSelf(bool in_enable, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus processBufferNative(std::vector<float>* in_monoBuffer, int32_t in_nFrames) = 0;
  virtual ::ndk::ScopedAStatus convertAndPlay(const std::string& in_source, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus cancelConvertAndPlay(::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus enableOfflineMode(bool in_offlineMode, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus setBypass(bool in_enable, ::aidl::net::voicemod::vmgramservice::VmgsResultAidl* _aidl_return) = 0;
private:
  static std::shared_ptr<IVMGramAidl> default_impl;
};
class IVMGramAidlDefault : public IVMGramAidl {
public:
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
  ::ndk::SpAIBinder asBinder() override;
  bool isRemote() override;
};
}  // namespace vmgramservice
}  // namespace voicemod
}  // namespace net
}  // namespace aidl
