package com.tecknobit.nova.helpers

import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.nova.screens.SplashScreen.Companion.activeLocalSession
import java.util.*

actual fun checkForUpdates(
    destinationScreen: String
) {
    /*FakeUpdaterDialog(
        config = OctocatKDUConfig(
            locale = Locale.getDefault(),
            appName = stringResource(Res.string.app_name),
            currentVersion = stringResource(Res.string.app_version),
            onUpdateAvailable = {

            },
            dismissAction = {

            }
        )
    )*/
    /*UpdaterDialog(
        locale = Locale.getDefault(),
        onUpdateAvailable = {
            launchApp = false
        },
        dismissAction = {
            launchApp = true
        }
    )*/
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
    Locale.setDefault(Locale.forLanguageTag(tag))
}