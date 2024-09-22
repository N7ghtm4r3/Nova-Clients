package com.tecknobit.nova.helpers.utils.ui

import android.content.Context
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.tecknobit.nova.ui.theme.gray_background
import com.tecknobit.nova.ui.theme.md_theme_light_primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The {@code SnackbarLauncher} class is useful to display the [Snackbar] component
 *
 * @param context: the context where the [SnackbarLauncher] has been invoked
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class SnackbarLauncher(
    private val context: Context
) {

    /**
     * ***scope* the coroutine used to launch the [Snackbar]
     */
    private lateinit var scope: CoroutineScope

    /**
     * ***snackbarHostState* the host of the [Snackbar]
     */
    private lateinit var snackbarHostState: SnackbarHostState

    /**
     * Function to init the [Snackbar] instances: [scope] and [snackbarHostState]
     *
     * No-any params required
     */
    @Composable
    fun InitSnackbarInstances() {
        scope = rememberCoroutineScope()
        snackbarHostState = remember { SnackbarHostState() }
    }

    /**
     * Function to init the [SnackbarHostState]
     *
     * No-any params required
     */
    @Composable
    fun CreateSnackbarHost() {
        SnackbarHost(hostState = snackbarHostState) {
            Snackbar(
                containerColor = md_theme_light_primary,
                contentColor = gray_background,
                snackbarData = it
            )
        }
    }

    /**
     * Function to display a [Snackbar] with an error message
     *
     * @param message: the error message to display
     * @param isError: state used to indicate whether is in error
     */
    fun showSnackError(
        message: String,
        isError: MutableState<Boolean>
    ) {
        showSnack(message)
        isError.value = true
    }

    /**
     * Function to display a [Snackbar] with an error message
     *
     * @param message: the error message to display
     * @param isError: state used to indicate whether is in error
     */
    fun showSnackError(
        message: Int,
        isError: MutableState<Boolean>
    ) {
        showSnack(context.getString(message))
        isError.value = true
    }

    /**
     * Function to display a [Snackbar] with a message
     *
     * @param message: the message to display
     */
    fun showSnack(
        message: String
    ) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

}