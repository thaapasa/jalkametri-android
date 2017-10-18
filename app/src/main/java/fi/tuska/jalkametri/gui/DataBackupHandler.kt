package fi.tuska.jalkametri.gui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.widget.Toast
import fi.tuska.jalkametri.Common.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.DataBackup
import fi.tuska.jalkametri.data.FileDataBackup
import fi.tuska.jalkametri.db.DBAdapter
import fi.tuska.jalkametri.gui.TaskExecutor.execute
import fi.tuska.jalkametri.util.LogUtil

object DataBackupHandler {

    private val TAG = "BackupDataHandler"

    fun backupData(activity: Activity) {

        if (checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermissions(activity, arrayOf(WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
            return
        }

        // Show confirmation; run DataBackupRunnable if OK is pressed
        Confirmation.showConfirmation(activity, R.string.title_confirm,
                R.string.backup_confirm_backup, R.string.backup_backup_understood,
                DataBackupRunnable(activity, FileDataBackup(DBAdapter(activity))),
                R.string.backup_backup_cancel, null)
    }

    private class DataBackupRunnable(private val activity: Activity, private val backupService: DataBackup) : Runnable {

        override fun run() {
            // Initiate data backup
            LogUtil.d(TAG, "Initiating data backup")

            val targetName = if (backupService.isBackupServiceAvailable) backupService.generateDataBackupName() else null

            if (targetName == null) {
                Toast.makeText(activity,
                        activity.resources.getString(R.string.backup_service_not_available),
                        Toast.LENGTH_LONG).show()
                return
            }

            execute(activity, R.string.backup_backing_up_data, {
                LogUtil.i(TAG, "Backing up data to file %s", targetName)
                backupService.backupData(targetName)
            }) { result ->
                if (result) {
                    // Success
                    LogUtil.i(TAG, "Data backed up")
                    Toast.makeText(activity, R.string.backup_backup_success, Toast.LENGTH_LONG).show()
                } else {
                    // Failure
                    LogUtil.w(TAG, "Data backup failed")
                    Toast.makeText(activity, R.string.backup_backup_failed, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    fun restoreData(context: Context) {
        // Initiate data restore
        val adapter = DBAdapter(context)
        val backup = FileDataBackup(adapter)
        LogUtil.d(TAG, "Initiating data restore")
        if (!backup.isBackupServiceAvailable) {
            Toast.makeText(context,
                    context.resources.getString(R.string.backup_service_not_available),
                    Toast.LENGTH_LONG).show()
            return
        }

        // Get backups
        val backups = backup.backups
        if (backups.isEmpty()) {
            Toast.makeText(context, R.string.backup_no_backups, Toast.LENGTH_LONG).show()
            return
        }

        // Show a dialog for selecting the backup to restore
        val items = backups.toTypedArray()

        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.backup_select_backup))
        builder.setItems(items) { dialog, item ->
            val fromBackup = items[item]
            dialog.dismiss()
            Confirmation.showConfirmation(context, R.string.backup_confirm_restore,
                    DataRestoreRunnable(fromBackup, context, backup))
        }
        val alert = builder.create()
        alert.setCancelable(true)
        alert.show()
    }

    private class DataRestoreRunnable(private val fromBackup: String, private val context: Context, private val backupService: DataBackup) : Runnable {

        override fun run() {
            execute(context, R.string.backup_restoring_data, {
                LogUtil.i(TAG, "Restoring data backup from file %s", fromBackup)
                backupService.restoreBackup(fromBackup)
            }) { value ->
                if (value) {
                    // Success
                    LogUtil.i(TAG, "Data restored")
                    Toast.makeText(context, R.string.backup_restore_success, Toast.LENGTH_LONG).show()
                } else {
                    // Failure
                    LogUtil.w(TAG, "Data restore failed")
                    Toast.makeText(context, R.string.backup_restore_failed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}