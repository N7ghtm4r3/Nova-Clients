package com.tecknobit.nova

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope
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
import com.tecknobit.nova.helpers.utils.startNotificationsFetching
import com.tecknobit.nova.helpers.utils.stopNotificationsFetching
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import moe.tlaster.precompose.ProvidePreComposeLocals
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.app_name
import nova.composeapp.generated.resources.logo
import nova.composeapp.generated.resources.open_nova
import nova.composeapp.generated.resources.quit_nova
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.io.File

fun main() = application {
    val isOpen = remember { mutableStateOf(true) }
    if (isOpen.value) {
        WindowMode(
            isOpen = isOpen
        )
    } else {
        this.TrayMode(
            isOpen = isOpen
        )
    }
}

@Composable
@NonRestartableComposable
private fun WindowMode(
    isOpen: MutableState<Boolean>
) {
    startNotificationsFetching()
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
}

@Composable
@NonRestartableComposable
private fun InitInstances() {
    localSessionsHelper = LocalSessionHelper(
        databaseDriverFactory = DatabaseDriverFactory()
    )
    setUpSession(
        hasBeenDisconnectedAction = { localSessionsHelper.logout() }
    )
}

@Composable
@NonRestartableComposable
private fun ApplicationScope.TrayMode(
    isOpen: MutableState<Boolean>
) {
    initNotifier()
    val notificationChecker = NotificationChecker()
    notificationChecker.execCheckRoutine()
    val trayState = rememberTrayState()
    Tray(
        state = trayState,
        icon = painterResource(Res.drawable.logo),
        menu = {
            Item(
                text = stringResource(Res.string.open_nova),
                onClick = { isOpen.value = true }
            )
            Item(
                text = stringResource(Res.string.quit_nova),
                onClick = { exitApplication() }
            )
        }
    )
}

private fun initNotifier() {
    stopNotificationsFetching()
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = true,
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "logo.png"
        )
    )
}