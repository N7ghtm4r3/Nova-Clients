package com.tecknobit.nova.helpers.utils

import androidx.compose.runtime.Composable
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

@Volatile
private var fetchNotifications: Boolean = true

fun isNotificationsFetchingEnable(): Boolean {
    return fetchNotifications
}

fun startNotificationsFetching() {
    fetchNotifications = true
}

fun stopNotificationsFetching() {
    fetchNotifications = false
}

fun notificationsFetching(): Boolean {
    return fetchNotifications
}

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

fun launchApp(
    destinationScreen: String
) {
    navigator.navigate(destinationScreen)
}