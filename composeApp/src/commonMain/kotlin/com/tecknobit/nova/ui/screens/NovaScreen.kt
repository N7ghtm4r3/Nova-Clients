package com.tecknobit.nova.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tecknobit.apimanager.annotations.Structure
import com.tecknobit.equinoxcompose.helpers.session.EquinoxScreen
import com.tecknobit.nova.navigator

@Structure
abstract class NovaScreen: EquinoxScreen() {

    companion object {

        const val SPLASH_SCREEN = "Splashscreen"

        const val AUTH_SCREEN = "AuthScreen"

        const val PROFILE_SCREEN = "ProfileScreen"

        const val PROFILE_SCREEN_DIALOG = "ProfileScreenDialog"

        const val PROJECTS_SCREEN = "ProjectsScreen"

        const val PROJECT_SCREEN = "ProjectScreen"

        const val RELEASE_SCREEN = "ReleaseScreen"

    }

    /**
     * *snackbarHostState* -> the host to launch the snackbar messages
     */
    protected val snackbarHostState = SnackbarHostState()

    @Composable
    protected open fun CollectStates() {

    }

    @Composable
    @NonRestartableComposable
    protected fun NavBackButton(
        modifier: Modifier = Modifier
    ) {
        IconButton(
            modifier = modifier,
            onClick = { navigator.goBack() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
    }

}