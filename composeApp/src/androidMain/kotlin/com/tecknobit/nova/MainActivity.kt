package com.tecknobit.nova

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.annotation.CallSuper
import androidx.annotation.ContentView
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

/**
 * The [MainActivity] is used as entry point of Nova's application for Android
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ComponentActivity
 *
 */
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

    /**
     * {@inheritDoc}
     *
     * If your ComponentActivity is annotated with [ContentView], this will
     * call [setContentView] for you.
     */
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InitInstances()
            App()
        }
    }

    /**
     * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when
     * the activity had been stopped, but is now again being displayed to the
     * user. It will usually be followed by {@link #onResume}. This is a good place to begin
     * drawing visual elements, running animations, etc.
     *
     * <p>You can call {@link #finish} from within this function, in
     * which case {@link #onStop} will be immediately called after {@link #onStart} without the
     * lifecycle transitions in-between ({@link #onResume}, {@link #onPause}, etc) executing.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onCreate
     * @see #onStop
     * @see #onResume
     */
    @CallSuper
    override fun onStart() {
        super.onStart()
        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()
    }

    /**
     * Function to init the instances for the session
     *
     * No-any params required
     */
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