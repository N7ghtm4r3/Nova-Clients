package com.tecknobit.nova.helpers.utils

import OctocatKDUConfig
import UpdaterDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.AUTH_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import nova.composeapp.generated.resources.Res.string
import nova.composeapp.generated.resources.app_name
import nova.composeapp.generated.resources.app_version
import org.jetbrains.compose.resources.stringResource
import java.util.Locale

/**
 * Function to check whether are available any updates for **Desktop** platform with the [UpdaterDialog]
 * component and then launch the application which the correct first screen to display
 *
 * No-any params required
 */
@Composable
@NonRestartableComposable
actual fun CheckForUpdatesAndLaunch() {
    var launchApp by remember { mutableStateOf(true) }
    UpdaterDialog(
        config = OctocatKDUConfig(
            locale = Locale.getDefault(),
            appName = stringResource(string.app_name),
            currentVersion = stringResource(string.app_version),
            onUpdateAvailable = { launchApp = false },
            dismissAction = { launchApp = true }
        )
    )
    if(launchApp) {
        val sessionInitialized = initSession()
        launchApp(
           destinationScreen = if(sessionInitialized)
               PROJECTS_SCREEN
           else
               AUTH_SCREEN
        )
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
    Locale.setDefault(Locale.forLanguageTag(tag))
}