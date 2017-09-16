package fi.tuska.jalkametri.gui;

import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DataBackup;
import fi.tuska.jalkametri.data.FileDataBackup;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.gui.TaskExecutor.Result;
import fi.tuska.jalkametri.gui.TaskExecutor.Task;
import fi.tuska.jalkametri.util.LogUtil;

public class DataBackupHandler {

    private static final String TAG = "BackupDataHandler";

    public static void backupData(Context context) {
        // Show confirmation; run DataBackupRunnable if OK is pressed
        Confirmation.showConfirmation(context, R.string.title_confirm,
            R.string.backup_confirm_backup, R.string.backup_backup_understood,
            new DataBackupRunnable(context, new FileDataBackup(new DBAdapter(context))),
            R.string.backup_backup_cancel, null);

    }

    private static class DataBackupRunnable implements Runnable {

        private final Context context;
        private final DataBackup backupService;

        private DataBackupRunnable(Context context, DataBackup backupService) {
            this.context = context;
            this.backupService = backupService;
        }

        @Override
        public void run() {
            // Initiate data backup
            LogUtil.d(TAG, "Initiating data backup");
            if (!backupService.isBackupServiceAvailable()) {
                Toast.makeText(context,
                    context.getResources().getString(R.string.backup_service_not_available),
                    Toast.LENGTH_LONG).show();
                return;
            }

            TaskExecutor.execute(context, R.string.backup_backing_up_data, new Task<Boolean>() {
                @Override
                public Boolean runTask() {
                    String targetName = backupService.generateDataBackupName();
                    LogUtil.i(TAG, "Backing up data to file %s", targetName);
                    return backupService.backupData(targetName);
                }
            }, new Result<Boolean>() {
                @Override
                public void processResult(Boolean result) {
                    if (result != null && result.booleanValue()) {
                        // Success
                        LogUtil.i(TAG, "Data backed up");
                        Toast
                            .makeText(context, R.string.backup_backup_success, Toast.LENGTH_LONG)
                            .show();
                    } else {
                        // Failure
                        LogUtil.w(TAG, "Data backup failed");
                        Toast.makeText(context, R.string.backup_backup_failed, Toast.LENGTH_LONG)
                            .show();

                    }

                }
            });
        }
    }

    public static void restoreData(final Context context) {
        // Initiate data restore
        DBAdapter adapter = new DBAdapter(context);
        final DataBackup backup = new FileDataBackup(adapter);
        LogUtil.d(TAG, "Initiating data restore");
        if (!backup.isBackupServiceAvailable()) {
            Toast.makeText(context,
                context.getResources().getString(R.string.backup_service_not_available),
                Toast.LENGTH_LONG).show();
            return;
        }

        // Get backups
        Set<String> backups = backup.getBackups();
        if (backups.isEmpty()) {
            Toast.makeText(context, R.string.backup_no_backups, Toast.LENGTH_LONG).show();
            return;
        }

        // Show a dialog for selecting the backup to restore
        final String[] items = backups.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.backup_select_backup));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String fromBackup = items[item];
                dialog.dismiss();
                Confirmation.showConfirmation(context, R.string.backup_confirm_restore,
                    new DataRestoreRunnable(fromBackup, context, backup));
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(true);
        alert.show();
    }

    private static class DataRestoreRunnable implements Runnable {

        private final String fromBackup;
        private final Context context;
        private final DataBackup backupService;

        private DataRestoreRunnable(String backup, Context context, DataBackup backupService) {
            this.fromBackup = backup;
            this.context = context;
            this.backupService = backupService;
        }

        @Override
        public void run() {
            TaskExecutor.execute(context, R.string.backup_restoring_data, new Task<Boolean>() {
                @Override
                public Boolean runTask() {
                    LogUtil.i(TAG, "Restoring data backup from file %s", fromBackup);
                    return backupService.restoreBackup(fromBackup);
                }
            }, new Result<Boolean>() {
                @Override
                public void processResult(Boolean value) {
                    if (value != null && value.booleanValue()) {
                        // Success
                        LogUtil.i(TAG, "Data restored");
                        Toast.makeText(context, R.string.backup_restore_success,
                            Toast.LENGTH_LONG).show();
                    } else {
                        // Failure
                        LogUtil.w(TAG, "Data restore failed");
                        Toast
                            .makeText(context, R.string.backup_restore_failed, Toast.LENGTH_LONG)
                            .show();
                    }
                }
            });
        }
    }

}
