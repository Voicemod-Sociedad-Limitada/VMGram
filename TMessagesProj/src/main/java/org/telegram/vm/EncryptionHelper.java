package org.telegram.vm;

import org.telegram.messenger.BuildConfig;

public class EncryptionHelper {

    public static String getTunaClientID() {
        return xorString(BuildConfig.TUNA_CLIENT_ID, BuildConfig.OBFUSCATION_XOR_KEY);
    }

    public static String getVoicemodUSDKLicenseKey() {
        return xorString(BuildConfig.VOICEMOD_USDK_LICENSE_KEY, BuildConfig.OBFUSCATION_XOR_KEY);
    }

    public static String getMParticleAPIKey() {
        return xorString(BuildConfig.MPARTICLE_API_KEY, BuildConfig.OBFUSCATION_XOR_KEY);
    }

    public static String getMParticleSecret() {
        return xorString(BuildConfig.MPARTICLE_SECRET, BuildConfig.OBFUSCATION_XOR_KEY);
    }

    public static String xorString(char[] input, String key) {
        return new String(xorChar(input, key));
    }

    public static char[] xorChar(char[] input, String key) {
        char[] keyChars = key.toCharArray();
        char[] output = new char[input.length];;

        for(int i = 0; i < input.length; i++) {
            output[i] = ((char) (input[i] ^ keyChars[i % keyChars.length]));
        }

        return output;
    }

}
