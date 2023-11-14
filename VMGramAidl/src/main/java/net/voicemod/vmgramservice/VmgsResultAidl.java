// This is the source code of VMGramClient
// It is licensed under GNU GPL v. 2 or later.
// You should have received a copy of the license in this archive (see LICENSE).
// Copyright Voicemod S.L, 2023.

package net.voicemod.vmgramservice;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public enum VmgsResultAidl implements Parcelable { // TODO: Remove Aidl suffix
    vcmdNoError(0),                       ///< Everything is expected to work correctly
    vcmdUndefinedError(-100),             ///< A standard exception has been thrown
    vcmdInternalException(-99),           ///< An internal exception has been thrown from our code
    vcmdInvalidInputSampleRate(-98),      ///< The input sample rate is invalid
    vcmdNoInitializedSDK(-97),            ///< initSDK() has not been called
    vcmdIdNotFound(-96),                  ///< Could not find any SDK identifier with the given value
    vcmdFileNotFound(-95),                ///< The file doesn't exist
    vcmdInitializedSDK(-94),              ///< No need to call initSDK()
    vcmdNoLoadedVoice(-93),               ///< There was an issue when calling loadVoice()
    vcmdNoPublicParam(-92),               ///< An attempt was made to modify a private parameter
    vcmdNoValidParam(-91),                ///< The parameter does not exist for the given voice
    vcmdNullPointer(-90),                 ///< A null pointer was encountered
    vcmdNoValidArgument(-89),             ///< An invalid value was passed as an argument
    vcmdSDKLicenseError(-88),             ///< An SDK license issue occurred. Call getLicenseStatus() for more information.
    vcmdIncorrectPath(-87),               ///< The path passed as argument is invalid or does not exist
    vcmdFileWriteError(-86),              ///< The path passed as argument does not have write permissions
    vcmdSDKNoMoreInstancesAvailable(-85), ///< Cannot create more SDK instances
    vcmdSDKNonExistentInstanceId(-84),    ///< An SDK instance does not exist for the given ID
    vcmdFileReadError(-83),               ///< Error reading file at the path passed as argument
    vcmdIncorrectFormat(-82),             ///< Incorrect format of the file at path passed as argument
    vcmdVMGramServiceException(-1)        ///< An exception occurred with the VMGramService communication
    ;

    private final int value;
    VmgsResultAidl(int v){ value = v; }

    // Map for avoid call new on Creator<VcmdResult>::createFromParcel

    private static final Map<Integer, VmgsResultAidl> map = new HashMap<Integer, VmgsResultAidl>(VmgsResultAidl.values().length);
    private static VmgsResultAidl valueOf(int pageType) { return map.get(pageType); }
    static {
        for (VmgsResultAidl pageType : VmgsResultAidl.values()) {
            map.put(pageType.value, pageType);
        }
    }

    // Parcelable

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(value);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<VmgsResultAidl> CREATOR = new Creator<VmgsResultAidl>() {
        @Override public VmgsResultAidl createFromParcel(Parcel in) {
            return VmgsResultAidl.valueOf(in.readInt());
        }
        @Override public VmgsResultAidl[] newArray(int size) {
            return new VmgsResultAidl[size];
        }
    };

}
