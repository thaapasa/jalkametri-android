package fi.tuska.jalkametri.data;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import android.os.Environment;
import fi.tuska.jalkametri.dao.DataBackup;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.util.FileUtil;
import fi.tuska.jalkametri.util.LogUtil;

public class FileDataBackup implements DataBackup {

    private static final String TAG = "FileDataBackup";

    private final DBAdapter adapter;

    public FileDataBackup(DBAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public String generateDataBackupName() {
        if (!isBackupServiceAvailable())
            return null;
        Set<String> backups = getBackups();
        int tryNum = 1;
        while (true) {
            String name = String.format("jAlcoMeter-backup.%1$tY-%1$tm-%1$td.%2$d.db",
                new Date(), tryNum);
            if (!backups.contains(name))
                return name;
            tryNum++;
        }
    }

    @Override
    public boolean backupData(String targetName) {
        if (!isBackupServiceAvailable())
            return false;

        try {
            adapter.lockDatabase();
            File backupFile = new File(getBackupDirectory(), targetName);
            File dbFile = getDatabaseFile();
            FileUtil.copyFile(dbFile, backupFile);
            return true;
        } catch (IOException e) {
            LogUtil.w(TAG, "Error when restoring backup file: %s (%s)", e.getMessage(), e);
            return false;
        } finally {
            adapter.unlockDatabase();
        }
    }

    @Override
    public Set<String> getBackups() {
        Set<String> backups = new TreeSet<String>(BACKUP_ORDER);
        if (!isBackupServiceAvailable())
            return backups;

        try {
            File backupDir = getBackupDirectory();
            File[] fileList = backupDir.listFiles();
            for (File file : fileList) {
                if (file.isFile())
                    backups.add(file.getName());
            }
            return backups;
        } catch (IOException e) {
            LogUtil.w(TAG, "Error reading backup directory: %s (%s)", e.getMessage(), e);
            return backups;
        }
    }

    @Override
    public boolean restoreBackup(String backupName) {
        if (!isBackupServiceAvailable())
            return false;

        try {
            adapter.lockDatabase();
            File backupFile = new File(getBackupDirectory(), backupName);
            if (!backupFile.exists() || !backupFile.isFile()) {
                LogUtil.w(TAG, "Backup file %s does not exist or is not a file", backupFile);
                return false;
            }
            File dbFile = getDatabaseFile();
            FileUtil.copyFile(backupFile, dbFile);
            return true;
        } catch (IOException e) {
            LogUtil.w(TAG, "Error when restoring backup file: %s (%s)", e.getMessage(), e);
            return false;
        } finally {
            adapter.unlockDatabase();
        }
    }

    @Override
    public boolean isBackupServiceAvailable() {
        // Need to have a mounted external storage
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private File getDatabaseFile() throws IOException {
        LogUtil.d(TAG, "Data dir is " + Environment.getDataDirectory());
        File dbFile = new File(Environment.getDataDirectory()
            + "/data/fi.tuska.jalkametri/databases/" + adapter.getDatabaseFilename());
        if (!dbFile.exists()) {
            LogUtil.w(TAG, "Database file %s does not exist", dbFile);
            throw new IOException("Database file does not exist");
        }
        return dbFile;
    }

    private File getBackupDirectory() throws IOException {
        if (!isBackupServiceAvailable())
            return null;
        File dir = new File(Environment.getExternalStorageDirectory(), "jAlcoMeter");
        if (!dir.exists()) {
            LogUtil.d(TAG, "Backup directory does not exist, creating %s", dir);
            if (!dir.mkdirs()) {
                LogUtil.w(TAG, "Backup dir doesn't exist and can't create: %s", dir);
                throw new IOException("Cannot create backup directory");
            }
            return dir;
        }

        if (!dir.isDirectory()) {
            LogUtil.w(TAG, "Backup directory is not a directory: %s", dir);
            throw new IOException("Backup directory is not a directory");
        }
        return dir;
    }

    /**
     * Comparator that orders strings in reverse order.
     */
    private static final Comparator<String> BACKUP_ORDER = new Comparator<String>() {
        @Override
        public int compare(String object1, String object2) {
            // Reverse order
            return object1 != null ? -object1.compareTo(object2) : -1;
        }
    };

}
