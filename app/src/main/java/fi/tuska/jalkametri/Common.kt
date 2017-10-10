package fi.tuska.jalkametri

/**
 * Common stuff: activity codes etc.
 */
object Common {

    /**
     * Enables developer functionality.
     */
    const val DEVELOPER_FUNCTIONALITY_ENABLED = true

    /**
     * Android Market publishing guide at
     * http://developer.android.com/guide/publishing/preparing.html says that
     * logging should be "deactivated" for applications that are published to
     * the App Market.
     *
     *
     * jAlkaMetri checks whether to do logging by checking this variable.
     */
    const val LOGGING_ENABLED = true

    const val ASSERTIONS_ENABLED = true

    /* Activity codes */
    const val ACTIVITY_CODE_ADD_CATEGORY = 0x00DA0001
    const val ACTIVITY_CODE_EDIT_CATEGORY = 0x00DA0002
    const val ACTIVITY_CODE_SELECT_DRINK = 0x00DA0003
    const val ACTIVITY_CODE_SELECT_DRINK_DETAILS = 0x00DA0004
    const val ACTIVITY_CODE_SELECT_DRINK_TYPE = 0x00DA0005
    const val ACTIVITY_CODE_SELECT_DRINK_SIZE = 0x00DA0006
    const val ACTIVITY_CODE_ADD_DRINK_SIZE = 0x00DA0007
    const val ACTIVITY_CODE_MODIFY_DRINK_SIZE = 0x00DA0008

    const val ACTIVITY_CODE_MODIFY_DRINK_EVENT = 0x00DA0009
    const val ACTIVITY_CODE_ADD_DRINK_FOR_DAY = 0x00DA000A
    const val ACTIVITY_CODE_CREATE_DRINK = 0x00DA000B
    const val ACTIVITY_CODE_MODIFY_DRINK = 0x00DA000C

    const val ACTIVITY_CODE_SHOW_PREFERENCES = 0x00500001
    const val ACTIVITY_CODE_SHOW_CALCULATOR = 0x00500002

    const val ACTIVITY_CODE_ADD_FAVOURITE = 0x00AC0001
    const val ACTIVITY_CODE_MODIFY_FAVOURITE = 0x00AC0002

    /* Default values */
    const val DEFAULT_ICON_NAME = "drink_generic"
    const val DEFAULT_SIZE_ICON_NAME = "drink_beer_pint"
    const val DEFAULT_ICON_RES = R.drawable.drink_generic

    /* Common keys */
    const val KEY_RESULT = "result"
    const val KEY_ORIGINAL = "original"

    /* Common format codes */
    const val SQL_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss"

    /* Constants */
    const val ALCOHOL_LITER_WEIGHT = 790.0

}

