package com.tecknobit.nova.helpers

import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.nova.MainActivity.Companion.appUpdateManager
import com.tecknobit.nova.MainActivity.Companion.launcher
import com.tecknobit.nova.helpers.utils.AppContext
import com.tecknobit.nova.screens.SplashScreen.Companion.activeLocalSession
import java.util.*

actual fun checkForUpdates(
    destinationScreen: String
) {
    appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
        val isUpdateAvailable = info.updateAvailability() == UPDATE_AVAILABLE
        val isUpdateSupported = info.isImmediateUpdateAllowed
        if(isUpdateAvailable && isUpdateSupported) {
            appUpdateManager.startUpdateFlowForResult(
                info,
                launcher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            )
        } //else
            //launchApp(intentDestination)
    }.addOnFailureListener {
        //launchApp(intentDestination)
    }
}

/**
 * Function to set locale language for the application
 *
 * No-any params required
 */
actual fun setLocale() {
    var tag: String = DEFAULT_LANGUAGE
    LANGUAGES_SUPPORTED.forEach { (key, value) ->
        if(value == activeLocalSession.language) {
            tag = key
            return@forEach
        }
    }
    val locale = Locale.forLanguageTag(tag)
    Locale.setDefault(locale)
    val resources = AppContext.get().resources
    val configuration = resources.configuration
    configuration.locale = locale
    resources.updateConfiguration(configuration, resources.displayMetrics)
}