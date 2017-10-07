package fi.tuska.jalkametri.util

object StringUtil {

    fun uppercaseFirstLetter(str: String?): String = when {
        str == null -> ""
        str.length <= 1 -> str.toUpperCase()
        else -> str.substring(0, 1).toUpperCase() + str.substring(1)
    }

}
