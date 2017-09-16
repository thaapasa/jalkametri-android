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
