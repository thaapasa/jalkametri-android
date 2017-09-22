package fi.tuska.jalkametri.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import fi.tuska.jalkametri.R;

public final class DialogUtil {

    private DialogUtil() {
        // No instantiation required
    }

    public static void showMessage(Context parent, int messageResource, int titleResource) {
        // Create the dialog
        Builder builder = new AlertDialog.Builder(parent)
            .setIcon(android.R.drawable.ic_dialog_info).setTitle(titleResource)
            .setMessage(messageResource).setPositiveButton(R.string.ok, null);
        // Show the dialog
        builder.show();
    }

    public static void showMessage(Context parent, String message, int titleResource) {
        // Create the dialog
        Builder builder = new AlertDialog.Builder(parent)
            .setIcon(android.R.drawable.ic_dialog_info).setTitle(titleResource)
            .setMessage(message).setPositiveButton(R.string.ok, null);
        // Show the dialog
        builder.show();
    }

    public static Dialog createMessageDialog(Context parent, int messageResource,
        int titleResource, int imageId) {
        // Create the dialog
        Builder builder = new AlertDialog.Builder(parent)
            .setIcon(android.R.drawable.ic_dialog_info).setTitle(titleResource)
            .setMessage(messageResource).setPositiveButton(R.string.ok, null);
        // Show the dialog
        return builder.create();
    }
}
