package org.telegram.vm;

import android.content.Context;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public class VMCallHelper {
    public static boolean isCallOngoing() {
        VoIPService voIPService = VoIPService.getSharedInstance();
        return (voIPService != null && voIPService.getCallerId() > 0);
    }

    public static long getCallerId() {
        VoIPService voIPService = VoIPService.getSharedInstance();
        return (voIPService != null ? voIPService.getCallerId() : 0);
    }

    public static TLRPC.User getCaller() {
        VoIPService voIPService = VoIPService.getSharedInstance();
        return voIPService.getUser();
    }

    public static void showCallInProgressDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(LocaleController.getString("ErrorOngoingCallTitle", R.string.ErrorOngoingCallTitle))
                .setMessage(LocaleController.getString("ErrorOngoingCallMessage", R.string.ErrorOngoingCallMessage))
                .setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
                    //Nothing to do
                })
                .show();
    }
}
