package com.tecknobit.nova.helpers.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.nova.MainActivity.Companion.DESTINATION_KEY
import com.tecknobit.nova.MainActivity.Companion.appUpdateManager
import com.tecknobit.nova.MainActivity.Companion.launcher
import com.tecknobit.nova.screens.NovaScreen.Companion.AUTH_SCREEN
import com.tecknobit.nova.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.screens.NovaScreen.Companion.PROJECT_SCREEN
import com.tecknobit.nova.screens.NovaScreen.Companion.RELEASE_SCREEN
import com.tecknobit.nova.screens.SplashScreen.Companion.activeLocalSession
import com.tecknobit.nova.screens.SplashScreen.Companion.localSessionsHelper
import com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_KEY
import kotlinx.coroutines.delay
import java.util.*

@Composable
actual fun CheckForUpdatesAndLaunch() {
    val intent = (LocalContext.current as Activity).intent
    LaunchedEffect(key1 = true) {
        delay(250)
        val projectId: String?
        when(intent.getStringExtra(DESTINATION_KEY)) {
            PROJECTS_KEY -> {
                localSessionsHelper.setNewActiveSession(intent.getStringExtra(IDENTIFIER_KEY)!!)
                initAndStartSession(
                    destinationScreen = PROJECTS_SCREEN
                )
            }
            PROJECT_KEY -> {
                localSessionsHelper.setNewActiveSession(intent.getStringExtra(IDENTIFIER_KEY)!!)
                projectId = intent.getStringExtra(PROJECT_IDENTIFIER_KEY)
                initAndStartSession(
                    destinationScreen = "$PROJECT_SCREEN/$projectId"
                )
            }
            RELEASE_KEY -> {
                localSessionsHelper.setNewActiveSession(intent.getStringExtra(IDENTIFIER_KEY)!!)
                projectId = intent.getStringExtra(PROJECT_IDENTIFIER_KEY)
                val releaseId = intent.getStringExtra(RELEASE_IDENTIFIER_KEY)
                initAndStartSession(
                    destinationScreen = "$RELEASE_SCREEN/$projectId/$releaseId"
                )
            }
            else -> { checkForUpdates() }
        }
    }
}

private fun checkForUpdates() {
    appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
        val isUpdateAvailable = info.updateAvailability() == UPDATE_AVAILABLE
        val isUpdateSupported = info.isImmediateUpdateAllowed
        if(isUpdateAvailable && isUpdateSupported) {
            appUpdateManager.startUpdateFlowForResult(
                info,
                launcher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            )
        } else {
            initAndStartSession(
                destinationScreen = PROJECTS_SCREEN
            )
        }
    }.addOnFailureListener {
        initAndStartSession(
            destinationScreen = PROJECTS_SCREEN
        )
    }
}

private fun initAndStartSession(
    destinationScreen: String
) {
    val sessionInitialized = initSession()
    launchApp(
        destinationScreen = if (!sessionInitialized)
            AUTH_SCREEN
        else
            destinationScreen
    )
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

actual fun refreshList() {
    /*
    if(activeLocalSession.isHostSet) {
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
                                for(j in 0 until jNotifications.length())
                                    notifications.add(NovaNotification(jNotifications.getJSONObject(j)))
                            },
                            onFailure = {}
                        )
                    }
                    delay(1000L)
                }
            }
        }
     */
}