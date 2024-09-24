package com.tecknobit.nova

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.tecknobit.nova.cache.LocalSessionHelper
import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory
import com.tecknobit.nova.helpers.utils.launchApp
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import io.github.vinceglb.filekit.core.FileKit

class MainActivity : ComponentActivity() {

    companion object {

        /**
         * {@code DESTINATION_KEY} the key for the <b>"destination"</b> field
         */
        const val DESTINATION_KEY = "destination"

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
        // TODO: TO FIX BEHAVIOR
        enableEdgeToEdge()
        initInstances()
        setContent {
            App()
        }
    }

    private fun initInstances() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        localSessionsHelper = LocalSessionHelper(
            databaseDriverFactory = DatabaseDriverFactory()
        )
        FileKit.init(this)
    }

}