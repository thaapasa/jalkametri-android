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
