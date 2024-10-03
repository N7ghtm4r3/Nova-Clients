package com.tecknobit.nova

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.mmk.kmpnotifier.permission.permissionUtil
import com.tecknobit.equinoxcompose.helpers.session.setUpSession
import com.tecknobit.nova.cache.LocalSessionHelper
import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory
import com.tecknobit.nova.helpers.utils.launchApp
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import com.tecknobit.nova.ui.theme.md_theme_light_primary
import io.github.vinceglb.filekit.core.FileKit

class MainActivity : ComponentActivity() {

    companion object {

        /**
         * **appUpdateManager** the manager to check if there is an update available
         */
        lateinit var appUpdateManager: AppUpdateManager

        /**
         * **launcher** the result registered for [appUpdateManager] and the action to execute if fails
         */
        lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>

    }

    init {
        launcher = registerForActivityResult(StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                launchApp(
                    destinationScreen = PROJECTS_SCREEN
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InitInstances()
            App()
        }
    }

    override fun onStart() {
        super.onStart()
        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()
    }

    @Composable
    private fun InitInstances() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        localSessionsHelper = LocalSessionHelper(
            databaseDriverFactory = DatabaseDriverFactory()
        )
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(
            color = md_theme_light_primary
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent
        )
        setUpSession(
            serverOfflineMessage = "server_currently_offline_message",
            noInternetConnectionMessage = "no_internet_message",
            noInternetConnectionIcon = ImageVector.vectorResource(id = R.drawable.no_internet),
            hasBeenDisconnectedAction = { localSessionsHelper.logout() }
        )
        FileKit.init(this)
    }

}