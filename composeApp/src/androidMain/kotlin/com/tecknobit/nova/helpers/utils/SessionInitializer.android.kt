package com.tecknobit.nova.helpers.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.nova.DESTINATION_KEY
import com.tecknobit.nova.MainActivity.Companion.appUpdateManager
import com.tecknobit.nova.MainActivity.Companion.launcher
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.AUTH_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECT_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.RELEASE_SCREEN
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_KEY
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Function to check whether are available any updates for **Android** platform and then launch the
 * application which the correct first screen to display
 *
 * No-any params required
 */
@Composable
@NonRestartableComposable
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

/**
 * Function to execute the updates checking with the [AppUpdateManager] and then, if there are not updates
 * available, invoking the [initAndStartSession] method to start the application
 *
 * No-any params required
 */
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

/**
 * Function to initialized the local session and then select the first screen to display
 *
 * @param destinationScreen: the screen to display if the session is initialized
 */
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