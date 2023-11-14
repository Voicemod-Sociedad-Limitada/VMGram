// This is the source code of VMGramClient
// It is licensed under GNU GPL v. 2 or later.
// You should have received a copy of the license in this archive (see LICENSE).
// Copyright Voicemod S.L, 2023.

#ifndef VOICEMODSERVICEAPP_PARCELABLEINTEGER_H
#define VOICEMODSERVICEAPP_PARCELABLEINTEGER_H

#include <android/binder_status.h>
#include <android/binder_parcel.h>

namespace aidl::net::voicemod::vmgramservice{

    class ParcelableInteger{

    public:

        int32_t value;

        binder_status_t readFromParcel(const AParcel* pParcel){
            AParcel_readInt32(pParcel, &value);
            return STATUS_OK;
        }

        binder_status_t writeToParcel(AParcel* pParcel) const {
            AParcel_writeInt32(pParcel, value);
            return STATUS_OK;
        }

    };

}

#endif //VOICEMODSERVICEAPP_PARCELABLEINTEGER_H
