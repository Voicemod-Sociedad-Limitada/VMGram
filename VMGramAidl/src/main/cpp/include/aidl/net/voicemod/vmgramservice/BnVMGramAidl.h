#pragma once

#include "aidl/net/voicemod/vmgramservice/VMGramAidl.h"

#include <android/binder_ibinder.h>

namespace aidl {
namespace net {
namespace voicemod {
namespace vmgramservice {
class BnVMGramAidl : public ::ndk::BnCInterface<IVMGramAidl> {
public:
  BnVMGramAidl();
  //WARNING: MAINTAIN THE INLINE DESTRUCTOR DECLARATION
  inline virtual ~BnVMGramAidl() = default;
protected:
  ::ndk::SpAIBinder createBinder() override;
private:
};
}  // namespace vmgramservice
}  // namespace voicemod
}  // namespace net
}  // namespace aidl
