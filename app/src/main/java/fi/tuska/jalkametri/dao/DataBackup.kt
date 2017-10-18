package fi.tuska.jalkametri.dao

interface DataBackup {

    /**
     * @return set of available data backups
     */
    val backups: Set<String>

    /**
     * Checks whether data can be backed up
     *
     * @return true if data can be backed up, false otherwise
     */
    val isBackupServiceAvailable: Boolean

    /**
     * @return an automatically generated name for a new data backup; null if data backup service is not available
     */
    fun generateDataBackupName(): String?

    /**
     * Backs up the data to the backup with the given name
     *
     * @param targetName the name of the backup
     * @return true on success, false on failure
     */
    fun backupData(targetName: String): Boolean

    /**
     * Restores the given data backup.
     *
     * @param backupName the backup to restore
     * @return true on success, false on failure
     */
    fun restoreBackup(backupName: String): Boolean

}
