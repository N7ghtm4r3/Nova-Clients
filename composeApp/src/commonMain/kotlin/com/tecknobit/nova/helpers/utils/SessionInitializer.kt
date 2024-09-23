package com.tecknobit.nova.helpers.utils

import androidx.compose.runtime.Composable
import com.tecknobit.nova.navigator
import com.tecknobit.nova.screens.SplashScreen.Companion.activeLocalSession
import com.tecknobit.nova.screens.SplashScreen.Companion.localSessionsHelper
import com.tecknobit.nova.screens.SplashScreen.Companion.requester

@Composable
expect fun CheckForUpdatesAndLaunch()

fun initSession(): Boolean {
    val activeSession = localSessionsHelper.activeSession
    val sessionInitialized = activeSession != null
    if(sessionInitialized) {
        activeLocalSession = activeSession!!
        requester = NovaRequester(
            userId = activeSession.id,
            userToken = activeSession.token,
            host = activeSession.hostAddress
        )
        setLocale()
        refreshList()
    } else {
        requester = NovaRequester(
            host = ""
        )
    }
    return sessionInitialized
}

 /**
 * Function to set locale language for the application
 *
 * No-any params required
 */
expect fun setLocale()

expect fun refreshList()

fun launchApp(
    destinationScreen: String
) {
    navigator.navigate(destinationScreen)
}