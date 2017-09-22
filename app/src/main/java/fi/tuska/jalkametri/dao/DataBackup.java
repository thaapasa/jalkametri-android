package fi.tuska.jalkametri.dao;

import java.util.Set;

/**
 * Interface for backing up and restoring the program data.
 *
 * @author Tuukka Haapasalo
 */
public interface DataBackup {

    /**
     * @return an automatically generated name for a new data backup
     */
    String generateDataBackupName();

    /**
     * Backs up the data to the backup with the given name
     *
     * @param targetName the name of the backup
     * @return true on success, false on failure
     */
    boolean backupData(String targetName);

    /**
     * @return set of available data backups
     */
    Set<String> getBackups();

    /**
     * Restores the given data backup.
     *
     * @param backupName the backup to restore
     * @return true on success, false on failure
     */
    boolean restoreBackup(String backupName);

    /**
     * Checks whether data can be backed up
     *
     * @return true if data can be backed up, false otherwise
     */
    boolean isBackupServiceAvailable();

}
