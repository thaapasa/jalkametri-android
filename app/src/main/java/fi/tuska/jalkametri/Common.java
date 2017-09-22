package fi.tuska.jalkametri;

/**
 * Common stuff: activity codes etc.
 *
 * @author Tuukka Haapasalo
 */
public interface Common {

    /*
     * Activity codes
     * ----------------------------------------------------------
     */
    int ACTIVITY_CODE_ADD_CATEGORY = 0x00DA0001;
    int ACTIVITY_CODE_EDIT_CATEGORY = 0x00DA0002;
    int ACTIVITY_CODE_SELECT_DRINK = 0x00DA0003;
    int ACTIVITY_CODE_SELECT_DRINK_DETAILS = 0x00DA0004;
    int ACTIVITY_CODE_SELECT_DRINK_TYPE = 0x00DA0005;
    int ACTIVITY_CODE_SELECT_DRINK_SIZE = 0x00DA0006;
    int ACTIVITY_CODE_ADD_DRINK_SIZE = 0x00DA0007;
    int ACTIVITY_CODE_MODIFY_DRINK_SIZE = 0x00DA0008;
    int ACTIVITY_CODE_MODIFY_DRINK_EVENT = 0x00DA0009;
    int ACTIVITY_CODE_ADD_DRINK_FOR_DAY = 0x00DA000A;
    int ACTIVITY_CODE_CREATE_DRINK = 0x00DA000B;
    int ACTIVITY_CODE_MODIFY_DRINK = 0x00DA000C;

    int ACTIVITY_CODE_SHOW_PREFERENCES = 0x00500001;
    int ACTIVITY_CODE_SHOW_CALCULATOR = 0x00500002;

    int ACTIVITY_CODE_ADD_FAVOURITE = 0x00AC0001;
    int ACTIVITY_CODE_MODIFY_FAVOURITE = 0x00AC0002;

    /*
     * Dialog codes
     * ----------------------------------------------------------
     */
    int DIALOG_SELECT_DATE = 0xD1A70001;
    int DIALOG_SHOW_DRINK_DETAILS = 0xD1A70002;
    int DIALOG_SELECT_ICON = 0xD1A70003;
    int DIALOG_SELECT_SIZE_ICON = 0xD1A70004;

    /*
     * Default values
     * ----------------------------------------------------------
     */
    String DEFAULT_ICON_NAME = "drink_generic";
    String DEFAULT_SIZE_ICON_NAME = "drink_beer_pint";
    int DEFAULT_ICON_RES = R.drawable.drink_generic;

    /*
     * Common keys ----------------------------------------------------------
     */
    String KEY_RESULT = "result";
    String KEY_ORIGINAL = "original";

    /*
     * Common format codes
     * ----------------------------------------------------------
     */
    String SQL_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    /*
     * Response codes for billing
     * ----------------------------------------------------------
     */
    // The response codes for a request, defined by Android Market.
    enum ResponseCode {
        RESULT_OK, RESULT_USER_CANCELED, RESULT_SERVICE_UNAVAILABLE, RESULT_BILLING_UNAVAILABLE,
        RESULT_ITEM_UNAVAILABLE, RESULT_DEVELOPER_ERROR, RESULT_ERROR;

        // Converts from an ordinal value to the ResponseCode
        public static ResponseCode valueOf(int index) {
            ResponseCode[] values = ResponseCode.values();
            if (index < 0 || index >= values.length) {
                return RESULT_ERROR;
            }
            return values[index];
        }
    }


    /*
     * Alcohol calculation constants
     * ----------------------------------------------------------
     */
    /** Weight of one liter of alcohol, in grams. */
    double ALCOHOL_LITER_WEIGHT = 790d;

    /*
     * Handler message keys
     * ----------------------------------------------------------
     */
    int MESSAGE_JOB_COMPLETE = 1;

    /*
     * Billing constants
     * ----------------------------------------------------------
     */
    // Intent actions that we send from the BillingReceiver to the
    // BillingService. Defined by this application.
    String ACTION_GET_PURCHASE_INFORMATION = "fi.tuska.jalkametri.GET_PURCHASE_INFORMATION";
    String ACTION_CONFIRM_NOTIFICATION = "com.example.dungeons.CONFIRM_NOTIFICATION";

    // Intent actions that we receive in the BillingReceiver from Market.
    // These are defined by Market and cannot be changed.
    String ACTION_NOTIFY = "com.android.vending.billing.IN_APP_NOTIFY";
    String ACTION_RESPONSE_CODE = "com.android.vending.billing.RESPONSE_CODE";
    String ACTION_PURCHASE_STATE_CHANGED = "com.android.vending.billing.PURCHASE_STATE_CHANGED";

    // These are the names of the extras that are passed in an intent from
    // Market to this application and cannot be changed.
    String NOTIFICATION_ID = "notification_id";
    String INAPP_SIGNED_DATA = "inapp_signed_data";
    String INAPP_SIGNATURE = "inapp_signature";
    String INAPP_REQUEST_ID = "request_id";
    String INAPP_RESPONSE_CODE = "response_code";

    // These are the names of the fields in the request bundle.
    String BILLING_REQUEST_METHOD = "BILLING_REQUEST";
    String BILLING_REQUEST_API_VERSION = "API_VERSION";
    String BILLING_REQUEST_PACKAGE_NAME = "PACKAGE_NAME";
    String BILLING_REQUEST_ITEM_ID = "ITEM_ID";
    String BILLING_REQUEST_DEVELOPER_PAYLOAD = "DEVELOPER_PAYLOAD";
    String BILLING_REQUEST_NOTIFY_IDS = "NOTIFY_IDS";
    String BILLING_REQUEST_NONCE = "NONCE";

    String BILLING_RESPONSE_RESPONSE_CODE = "RESPONSE_CODE";
    String BILLING_RESPONSE_PURCHASE_INTENT = "PURCHASE_INTENT";
    String BILLING_RESPONSE_REQUEST_ID = "REQUEST_ID";
    long BILLING_RESPONSE_INVALID_REQUEST_ID = -1;

    String BILLING_PACKAGE_NAME = "fi.tuska.jalkametri";

    // The possible states of an in-app purchase, as defined by Android
    // Market.
    enum PurchaseState {
        // Responses to requestPurchase or restoreTransactions.
        PURCHASED, // User was charged for the order.
        CANCELED, // The charge failed on the server.
        REFUNDED; // User received a refund for the order.

        // Converts from an ordinal value to the PurchaseState
        public static PurchaseState valueOf(int index) {
            PurchaseState[] values = PurchaseState.values();
            if (index < 0 || index >= values.length) {
                return CANCELED;
            }
            return values[index];
        }
    }

}
