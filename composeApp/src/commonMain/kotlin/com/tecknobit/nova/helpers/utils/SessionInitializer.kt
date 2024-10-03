package com.tecknobit.nova.helpers.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.notifications
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.records.NovaNotification
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **fetchNotifications** -> whether fetch the notifications of the user or not
 */
@Volatile
private var fetchNotifications: Boolean = true

/**
 * Function to get whether the notifications fetching is enabled
 *
 * No-any params required
 *
 * @return whether the notifications fetching is enabled as [Boolean]
 */
fun isNotificationsFetchingEnable(): Boolean {
    return fetchNotifications
}

/**
 * Function to start the notifications fetching
 *
 * No-any params required
 */
fun startNotificationsFetching() {
    fetchNotifications = true
}

/**
 * Function to start the notifications fetching
 *
 * No-any params required
 */
fun stopNotificationsFetching() {
    fetchNotifications = false
}

/**
 * Function to check whether are available any updates for each platform and then launch the application
 * which the correct first screen to display
 *
 * No-any params required
 */
@Composable
@NonRestartableComposable
expect fun CheckForUpdatesAndLaunch()

/**
 * Function to init the local session and the related instances
 *
 * No-any params required
 *
 * @return whether the local session has been initialized or not, so the user was not logged yet, as [Boolean]
 */
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
        fetchNotifications()
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

/**
 * Function to get the notifications of the user, this routine will continue whether the [fetchNotifications]
 * is set on *true* and the current local session is active
 *
 * No-any params required
 */
@OptIn(DelicateCoroutinesApi::class)
fun fetchNotifications() {
    GlobalScope.launch {
        while (fetchNotifications && activeLocalSession.isHostSet) {
            requester.sendRequest(
                request = { requester.getNotifications() },
                onSuccess = { response ->
                    val jNotifications = response.getJSONArray(RESPONSE_MESSAGE_KEY)
                    jNotifications?.let {
                        notifications.clear()
                        for (j in 0 until jNotifications.length()) {
                            val notification = NovaNotification(jNotifications.getJSONObject(j))
                            notifications.add(notification)
                        }
                    }
                },
                onFailure = {}
            )
            delay(5000L)
        }
    }
}

/**
 * Function to launch the application
 *
 * @param destinationScreen: the screen to display first
 */
fun launchApp(
    destinationScreen: String
) {
    navigator.navigate(destinationScreen)
}