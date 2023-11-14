// This is the source code of VMGramClient
// It is licensed under GNU GPL v. 2 or later.
// You should have received a copy of the license in this archive (see LICENSE).
// Copyright Voicemod S.L, 2023.

#ifndef VOICEMODSERVICEAPP_PARCELABLESTRING_H
#define VOICEMODSERVICEAPP_PARCELABLESTRING_H

#include <android/binder_status.h>
#include <android/binder_parcel_utils.h>

namespace aidl::net::voicemod::vmgramservice{

    class ParcelableString{

    public:

        std::string value;

        binder_status_t readFromParcel(const AParcel* pParcel){
            ndk::AParcel_readString(pParcel, &value);
            return STATUS_OK;
        }

        binder_status_t writeToParcel(AParcel* pParcel) const {
            ndk::AParcel_writeString(pParcel, value);
            return STATUS_OK;
        }

    };

}

#endif //VOICEMODSERVICEAPP_PARCELABLESTRING_H
