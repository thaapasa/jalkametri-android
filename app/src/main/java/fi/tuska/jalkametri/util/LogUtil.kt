package fi.tuska.jalkametri.util

import android.util.Log
import fi.tuska.jalkametri.Common.LOGGING_ENABLED

interface Logger {
    fun d(tag: String, message: String): Int
    fun i(tag: String, message: String): Int
    fun w(tag: String, message: String): Int
    fun e(tag: String, message: String): Int
}

object AndroidLogger : Logger {
    override fun d(tag: String, message: String) = Log.d(tag, message)
    override fun i(tag: String, message: String) = Log.i(tag, message)
    override fun w(tag: String, message: String) = Log.w(tag, message)
    override fun e(tag: String, message: String) = Log.e(tag, message)
}

object NoopLogger : Logger {
    override fun d(tag: String, message: String) = 0
    override fun i(tag: String, message: String) = 0
    override fun w(tag: String, message: String) = 0
    override fun e(tag: String, message: String) = 0
}

object LogUtil {

    var logger: Logger = AndroidLogger

    val isLoggingEnabled: Boolean = LOGGING_ENABLED

    private fun getLogTag(tag: String, message: String): String = "jAlcoMeter"

    private fun getMessage(tag: String, message: String): String = "$tag: $message"

    fun d(tag: String, message: String, vararg params: Any) {
        val msg = String.format(message, *params)
        if (isLoggingEnabled)
            logger.d(getLogTag(tag, msg), getMessage(tag, msg))
    }

    fun i(tag: String, message: String, vararg params: Any) {
        val msg = String.format(message, *params)
        if (isLoggingEnabled)
            logger.i(getLogTag(tag, msg), getMessage(tag, msg))
    }

    fun w(tag: String, message: String, vararg params: Any) {
        val msg = String.format(message, *params)
        if (isLoggingEnabled)
            logger.w(getLogTag(tag, msg), getMessage(tag, msg))
    }

    fun e(tag: String, message: String, vararg params: Any) {
        // Error messages are always logged
        val msg = String.format(message, *params)
        logger.e(getLogTag(tag, msg), getMessage(tag, msg))
    }

}
