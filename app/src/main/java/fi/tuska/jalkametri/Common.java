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
     * Alcohol calculation constants
     * ----------------------------------------------------------
     */
    /** Weight of one liter of alcohol, in grams. */
    double ALCOHOL_LITER_WEIGHT = 790d;

}
