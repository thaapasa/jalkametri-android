package fi.tuska.jalkametri.util

import fi.tuska.jalkametri.PrivateData

object AssertionUtils {

    val TAG = "AssertionUtils"

    val isAssertionsEnabled: Boolean = PrivateData.ASSERTIONS_ENABLED

    fun <T> expectEquals(v1: T, v2: T) {
        if (v1 != v2) {
            val e = AssertionError("$v1 != $v2")
            LogUtil.e(TAG, "Assertion failed: ${e.message}: ${e.stackTrace}")
            if (isAssertionsEnabled) {
                throw e
            }
        }
    }

    inline fun expect(cond: () -> Boolean) {
        if (!cond()) {
            val e = AssertionError()
            LogUtil.e(TAG, "Assertion failed: ${e.stackTrace}")
            if (isAssertionsEnabled) {
                throw e
            }
        }
    }

    fun expect(cond: Boolean) {
        if (!cond) {
            val e = AssertionError()
            LogUtil.e(TAG, "Assertion failed: ${e.stackTrace}")
            if (isAssertionsEnabled) {
                throw e
            }
        }
    }

}
