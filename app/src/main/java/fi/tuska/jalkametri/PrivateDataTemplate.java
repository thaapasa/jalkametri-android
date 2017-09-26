package fi.tuska.jalkametri;

/**
 * Private data. Change the name of this class to PrivateData, and configure
 * the constants to your liking.
 *
 * @author Tuukka Haapasalo
 */
public interface PrivateDataTemplate {

    /**
     * Enables developer functionality.
     */
    boolean DEVELOPER_FUNCTIONALITY_ENABLED = false;

    /**
     * Android Market publishing guide at
     * http://developer.android.com/guide/publishing/preparing.html says that
     * logging should be "deactivated" for applications that are published to
     * the App Market.
     *
     * jAlkaMetri checks whether to do logging by checking this variable.
     */
    boolean LOGGING_ENABLED = false;

}
