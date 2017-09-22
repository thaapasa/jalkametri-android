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

    /** AdMob publisher ID */
    String ADMOB_PUBLISHER_ID = "my-admob-publisher-id";

    /** The real product ID */
    String JALCOMETER_REAL_LICENSE_PRODUCT_ID = "product_id_in_android_market";
    /** Test product IDs */
    String TEST_PRODUCT_ID_PURCHASED = "android.test.purchased";
    String TEST_PRODUCT_ID_CANCELED = "android.test.canceled";
    String TEST_PRODUCT_ID_REFUNDED = "android.test.refunded";
    String TEST_PRODUCT_ID_ITEM_UNAVAILABLE = "android.test.item_unavailable";
    /** Product ID for the jAlcoMeter license */
    String LICENSE_PRODUCT_ID = JALCOMETER_REAL_LICENSE_PRODUCT_ID;

    /** License purchasing payload */
    String LICENSE_PURCHASE_PAYLOAD = "license_purchasing_payload";

    /** Form key of the Google Docs form where stack traces are sent */
    String STACK_TRACE_DOCS_FORM_KEY = "form_key_for_google_docs_document";

    /** The Market account public key for verification */
    String ACCOUNT_PUBLIC_KEY = "android_market_public_key";

}
