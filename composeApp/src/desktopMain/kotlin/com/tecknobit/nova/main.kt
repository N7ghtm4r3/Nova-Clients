package com.tecknobit.nova

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.tecknobit.nova.cache.LocalSessionHelper
import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import moe.tlaster.precompose.ProvidePreComposeLocals
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.app_name
import nova.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        icon = painterResource(Res.drawable.logo),
        title = stringResource(Res.string.app_name),
        onCloseRequest = ::exitApplication,
        state = WindowState(placement = WindowPlacement.Maximized)
    ) {
        localSessionsHelper = LocalSessionHelper(
            databaseDriverFactory = DatabaseDriverFactory()
        )
        ProvidePreComposeLocals {
            App()
        }
    }
}