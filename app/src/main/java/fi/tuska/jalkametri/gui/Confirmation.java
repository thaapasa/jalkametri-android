package fi.tuska.jalkametri.gui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import fi.tuska.jalkametri.R;

public final class Confirmation {

    private Confirmation() {
        // Prevent instantiation
    }

    /**
     * Shows a confirmation message to the user.
     *
     * @param parent the parent context
     * @param messageResource the resource identifier of the message string
     * @param actionOnOK the action to perform if user clicks OK
     */
    public static void showConfirmation(Context parent, int messageResource,
        final Runnable actionOnOK) {
        showConfirmation(parent, R.string.title_confirm, messageResource, R.string.yes,
            actionOnOK, R.string.no, null);
    }

    public static void showConfirmation(Context parent, int titleResource, int messageResource,
        int okResource, final Runnable actionOnOK, int cancelResource,
        final Runnable actionOnCancel) {
        // Create the dialog
        Builder builder = new AlertDialog.Builder(parent)
            .setIcon(android.R.drawable.ic_dialog_alert).setTitle(titleResource)
            .setMessage(messageResource)
            .setPositiveButton(okResource, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (actionOnOK != null)
                        actionOnOK.run();
                }
            }).setNegativeButton(cancelResource, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (actionOnCancel != null)
                        actionOnCancel.run();
                }
            });
        // Show the dialog
        builder.show();
    }

}
