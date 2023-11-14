package org.telegram.vm.utils;

public class KeyboardUtils {

    private static KeyboardVisibilityListener keyboardVisibilityListener;

    public static void onKeyboardVisibilityChanged(boolean visible, int keyboardHeight){
        if(keyboardVisibilityListener != null){
            keyboardVisibilityListener.onKeyboardVisibilityChanged(visible, keyboardHeight);
        }
    }

    public static void setKeyboardVisibilityListener(KeyboardVisibilityListener keyboardVisibilityListener) {
        KeyboardUtils.keyboardVisibilityListener = keyboardVisibilityListener;
    }

}
