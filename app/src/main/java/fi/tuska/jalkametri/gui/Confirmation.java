/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
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
