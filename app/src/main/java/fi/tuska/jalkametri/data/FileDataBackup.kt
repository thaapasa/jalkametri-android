package fi.tuska.jalkametri.data

import android.os.Environment
import fi.tuska.jalkametri.dao.DataBackup
import fi.tuska.jalkametri.db.DBAdapter
import fi.tuska.jalkametri.util.FileUtil
import fi.tuska.jalkametri.util.LogUtil
import java.io.File
import java.io.IOException
import java.util.Comparator
import java.util.Date
import java.util.Locale
import java.util.TreeSet

class FileDataBackup(private val adapter: DBAdapter) : DataBackup {

    private val databaseFile: File
        @Throws(IOException::class)
        get() {
            LogUtil.d(TAG, "Data dir is " + Environment.getDataDirectory())
            val dbFile = File(Environment.getDataDirectory().toString()
                    + "/data/fi.tuska.jalkametri/databases/" + adapter.databaseFilename)
            if (!dbFile.exists()) {
                LogUtil.w(TAG, "Database file %s does not exist", dbFile)
                throw IOException("Database file does not exist")
            }
            return dbFile
        }

    private val backupDirectory: File?
        @Throws(IOException::class)
        get() {
            if (!isBackupServiceAvailable)
                return null
            val dir = File(Environment.getExternalStorageDirectory(), "jAlcoMeter")
            if (!dir.exists()) {
                LogUtil.d(TAG, "Backup directory does not exist, creating %s", dir)
                if (!dir.mkdirs()) {
                    LogUtil.w(TAG, "Backup dir doesn't exist and can't create: %s", dir)
                    throw IOException("Cannot create backup directory")
                }
                return dir
            }

            if (!dir.isDirectory) {
                LogUtil.w(TAG, "Backup directory is not a directory: %s", dir)
                throw IOException("Backup directory is not a directory")
            }
            return dir
        }

    override fun generateDataBackupName(): String? {
        if (!isBackupServiceAvailable)
            return null
        val backups = backups
        var tryNum = 1
        while (true) {
            val name = String.format(Locale.ENGLISH, "jAlcoMeter-backup.%1\$tY-%1\$tm-%1\$td.%2\$d.db",
                    Date(), tryNum)
            if (!backups.contains(name))
                return name
            tryNum++
        }
    }

    override fun backupData(targetName: String): Boolean {
        if (!isBackupServiceAvailable)
            return false

        try {
            adapter.lockDatabase()
            val backupFile = File(backupDirectory, targetName)
            val dbFile = databaseFile
            FileUtil.copyFile(dbFile, backupFile)
            return true
        } catch (e: IOException) {
            LogUtil.w(TAG, "Error when restoring backup file: %s (%s)", e.message ?: "", e)
            return false
        } finally {
            adapter.unlockDatabase()
        }
    }

    override val backups: Set<String>
        get() {
            val backups = TreeSet(BACKUP_ORDER)
            if (!isBackupServiceAvailable)
                return backups

            return try {
                val backupDir = backupDirectory
                val fileList = backupDir!!.listFiles()
                fileList
                        .filter { it.isFile }
                        .mapTo(backups) { it.name }
                backups
            } catch (e: IOException) {
                LogUtil.w(TAG, "Error reading backup directory: %s (%s)", e.message ?: "", e)
                backups
            }

        }

    override fun restoreBackup(backupName: String): Boolean {
        if (!isBackupServiceAvailable)
            return false

        try {
            adapter.lockDatabase()
            val backupFile = File(backupDirectory, backupName)
            if (!backupFile.exists() || !backupFile.isFile) {
                LogUtil.w(TAG, "Backup file %s does not exist or is not a file", backupFile)
                return false
            }
            val dbFile = databaseFile
            FileUtil.copyFile(backupFile, dbFile)
            return true
        } catch (e: IOException) {
            LogUtil.w(TAG, "Error when restoring backup file: %s (%s)", e.message ?: "", e)
            return false
        } finally {
            adapter.unlockDatabase()
        }
    }

    override val isBackupServiceAvailable: Boolean
        get() {
            // Need to have a mounted external storage
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

    companion object {

        private val TAG = "FileDataBackup"

        /**
         * Comparator that orders strings in reverse order.
         */
        private val BACKUP_ORDER = Comparator<String> { object1, object2 ->
            // Reverse order
            if (object1 != null) -object1.compareTo(object2) else -1
        }
    }

}
