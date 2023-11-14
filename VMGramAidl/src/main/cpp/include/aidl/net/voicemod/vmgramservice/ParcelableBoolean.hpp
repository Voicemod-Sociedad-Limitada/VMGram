// This is the source code of VMGramClient
// It is licensed under GNU GPL v. 2 or later.
// You should have received a copy of the license in this archive (see LICENSE).
// Copyright Voicemod S.L, 2023.

#ifndef VOICEMODSERVICEAPP_PARCELABLEBOOLEAN_HPP
#define VOICEMODSERVICEAPP_PARCELABLEBOOLEAN_HPP

#include <android/binder_status.h>
#include <android/binder_parcel.h>

namespace aidl::net::voicemod::vmgramservice{

    class ParcelableBoolean{

    public:

        bool value;

        binder_status_t readFromParcel(const AParcel* pParcel){
            int32_t aux;
            AParcel_readInt32(pParcel, &aux);
            value = aux > 0 ? true : false;
            return STATUS_OK;
        }

        binder_status_t writeToParcel(AParcel* pParcel) const {
            AParcel_writeInt32(pParcel, value ? 1 : 0);
            return STATUS_OK;
        }

    };

}

#endif //VOICEMODSERVICEAPP_PARCELABLEBOOLEAN_HPP
