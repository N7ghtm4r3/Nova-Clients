package com.tecknobit.nova.helpers.utils

import OctocatKDUConfig
import UpdaterDialog
import androidx.compose.runtime.*
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.nova.screens.NovaScreen.Companion.AUTH_SCREEN
import com.tecknobit.nova.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.screens.SplashScreen.Companion.activeLocalSession
import nova.composeapp.generated.resources.Res.string
import nova.composeapp.generated.resources.app_name
import nova.composeapp.generated.resources.app_version
import org.jetbrains.compose.resources.stringResource
import java.util.*

@Composable
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

// TODO: TO IMPLEMENT
actual fun refreshList() {
    /*if(activeLocalSession.isHostSet) {
        refreshRoutine.launch {
            while (true) {
                if(!EXECUTING_REQUEST) {
                    requester.sendRequest(
                        request = {
                            requester.getNotifications()
                        },
                        onSuccess = { response ->
                            val jNotifications = response.getJSONArray(RESPONSE_MESSAGE_KEY)
                            notifications.clear()
                            for(j in 0 until jNotifications.length()) {
                                val notification = NovaNotification(jNotifications.getJSONObject(j))
                                if(!notification.isSent)
                                    notifications.add(notification)
                            }
                        },
                        onFailure = {}
                    )
                }
                delay(1000L)
            }
        }
    }*/
}