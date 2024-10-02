package com.tecknobit.nova

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import com.mmk.kmpnotifier.extensions.composeDesktopResourcesPath
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.tecknobit.equinoxcompose.helpers.session.setUpSession
import com.tecknobit.nova.cache.LocalSessionHelper
import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory
import com.tecknobit.nova.helpers.utils.NotificationChecker
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import moe.tlaster.precompose.ProvidePreComposeLocals
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.app_name
import nova.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.io.File

fun main() = application {
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = true,
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "logo.png"
        )
    )
    val isOpen = remember { mutableStateOf(true) }
    val trayState = rememberTrayState()
    if (isOpen.value) {
        Window(
            icon = painterResource(Res.drawable.logo),
            title = stringResource(Res.string.app_name),
            onCloseRequest = { isOpen.value = false },
            state = WindowState(placement = WindowPlacement.Maximized)
        ) {
            InitInstances()
            ProvidePreComposeLocals {
                App()
            }
        }
    } else {
        NotificationChecker().execCheckRoutine()
        Tray(
            state = trayState,
            icon = painterResource(Res.drawable.logo),
            menu = {
                Item(
                    "Open Nova",
                    onClick = { isOpen.value = true }
                )
                Item(
                    "Quit",
                    onClick = { exitApplication() }
                )
            }
        )
    }
}

@Composable
private fun InitInstances() {
    localSessionsHelper = LocalSessionHelper(
        databaseDriverFactory = DatabaseDriverFactory()
    )
    setUpSession(
        hasBeenDisconnectedAction = {
            // TODO: TO SET
        }
    )
}