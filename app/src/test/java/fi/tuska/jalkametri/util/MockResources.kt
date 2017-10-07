package fi.tuska.jalkametri.util

import android.content.res.Resources
import fi.tuska.jalkametri.R
import org.mockito.Mockito
import org.mockito.Mockito.`when` as When

object MockResources {

    fun en(): Resources = Mockito.mock(Resources::class.java).apply {
        When(getString(R.string.day_showday_format)).thenReturn("EE M/d")
        When(getString(R.string.time_format)).thenReturn("HH:mm")
        When(getString(R.string.day_full_format)).thenReturn("EE M/d/y HH:mm")
    }

    fun fi(): Resources = Mockito.mock(Resources::class.java).apply {
        When(getString(R.string.day_showday_format)).thenReturn("EE d.M.")
        When(getString(R.string.time_format)).thenReturn("HH:mm")
        When(getString(R.string.day_full_format)).thenReturn("EE d.M.yyyy HH:mm")
    }
}
