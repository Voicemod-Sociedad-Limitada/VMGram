// This is the source code of VMGramClient
// It is licensed under GNU GPL v. 2 or later.
// You should have received a copy of the license in this archive (see LICENSE).
// Copyright Voicemod S.L, 2023.

#ifndef VMGRAM_SERVICE_DEVELOP_APP_ENUMS_HPP
#define VMGRAM_SERVICE_DEVELOP_APP_ENUMS_HPP

namespace aidl::net::voicemod::vmgramservice{

    enum VmgsVoiceQuality {
        vmgsMediumQuality, ///< Medium voice quality.
        vmgsHighQuality    ///< High voice quality.
    };

    enum VmgsResult {
        vmgsNoError = 0,            ///< Everything is expected to work correctly
        vmgsServiceConnectionProblem = 1, ///< There was a problem with the connection to the server, the service may be disconnected.
        vmgsUndefinedError = -100,  ///< A standard exception has been thrown
        vmgsInternalException,      ///< An internal exception has been thrown from our code
        vmgsInvalidInputSampleRate, ///< The input sample rate is invalid
        vmgsNoInitializedSDK,       ///< initSDK() has not been called
        vmgsIdNotFound,             ///< Could not find any SDK identifier with the given value
        vmgsFileNotFound,           ///< The file doesn't exist
        vmgsInitializedSDK,         ///< No need to call initSDK()
        vmgsNoLoadedVoice,          ///< There was an issue when calling loadVoice()
        vmgsNoPublicParam,          ///< An attempt was made to modify a private parameter
        vmgsNoValidParam,           ///< The parameter does not exist for the given voice
        vmgsNullPointer,            ///< A null pointer was encountered
        vmgsNoValidArgument,        ///< An invalid value was passed as an argument
        vmgsSDKLicenseError,        ///< An SDK license issue occurred. Call getLicenseStatus() for more information.
        vmgsIncorrectPath,          ///< The path passed as argument is invalid or does not exist
        vmgsFileWriteError,         ///< The path passed as argument does not have write permissions
        vmgsSDKNoMoreInstancesAvailable, ///< Cannot create more SDK instances
        vmgsSDKNonExistentInstanceId,    ///< An SDK instance does not exist for the given ID
        vmgsFileReadError,               ///< Error reading file at the path passed as argument
        vmgsIncorrectFormat,             ///< Incorrect format of the file at path passed as argument
        vmgsPlatformNotSupported, ///< The implementation of a method is not supported on that platform
    };

}

#endif //VMGRAM_SERVICE_DEVELOP_APP_ENUMS_HPP
