package com.tecknobit.nova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tecknobit.apimanager.annotations.Structure
import com.tecknobit.equinox.environment.records.EquinoxItem
import com.tecknobit.equinoxcompose.components.EmptyListUI
import com.tecknobit.equinoxcompose.helpers.session.EquinoxScreen
import com.tecknobit.nova.helpers.utils.fetchNotifications
import com.tecknobit.nova.helpers.utils.isNotificationsFetchingEnable
import com.tecknobit.nova.helpers.utils.startNotificationsFetching
import com.tecknobit.nova.helpers.utils.stopNotificationsFetching
import com.tecknobit.nova.navigator
import nova.composeapp.generated.resources.Res.string
import nova.composeapp.generated.resources.loading_data

/**
 * The [NovaScreen] class is useful to provides the basic behavior of a Nova's UI screen
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxScreen
 */
@Structure
abstract class NovaScreen: EquinoxScreen() {

    companion object {

        /**
         * **SPLASH_SCREEN** -> route to navigate to the [Splashscreen]
         */
        const val SPLASH_SCREEN = "Splashscreen"

        /**
         * **AUTH_SCREEN** -> route to navigate to the [AuthScreen]
         */
        const val AUTH_SCREEN = "AuthScreen"

        /**
         * **PROFILE_SCREEN** -> route to navigate to the [ProfileScreen]
         */
        const val PROFILE_SCREEN = "ProfileScreen"

        /**
         * **PROFILE_DIALOG** -> route to navigate to the [ProfileScreen] as dialog
         */
        const val PROFILE_DIALOG = "ProfileDialog"

        /**
         * **ProjectsScreen** -> route to navigate to the [ProjectsScreen]
         */
        const val PROJECTS_SCREEN = "ProjectsScreen"

        /**
         * **WORK_ON_PROJECT_SCREEN** -> route to navigate to the [WorkOnProjectScreen]
         */
        const val WORK_ON_PROJECT_SCREEN = "WorkOnProjectScreen"

        /**
         * **WORK_ON_PROJECT_DIALOG** -> route to navigate to the [WorkOnProjectScreenDialog]
         */
        const val WORK_ON_PROJECT_DIALOG = "WorkOnProjectDialog"

        /**
         * **JOIN_PROJECT_SCREEN** -> route to navigate to the [JoinProjectScreen]
         */
        const val JOIN_PROJECT_SCREEN = "JoinProjectScreen"

        /**
         * **JOIN_PROJECT_DIALOG** -> route to navigate to the [JoinProjectScreen] as dialog
         */
        const val JOIN_PROJECT_DIALOG = "JoinProjectDialog"

        /**
         * **PROJECT_SCREEN** -> route to navigate to the [ProjectScreen]
         */
        const val PROJECT_SCREEN = "ProjectScreen"

        /**
         * **ADD_MEMBERS_SCREEN** -> route to navigate to the [AddMembersScreen]
         */
        const val ADD_MEMBERS_SCREEN = "AddMembersScreen"

        /**
         * **ADD_MEMBERS_DIALOG** -> route to navigate to the [AddMembersScreen] as dialog
         */
        const val ADD_MEMBERS_DIALOG = "AddMembersDialog"

        /**
         * **RELEASE_SCREEN** -> route to navigate to the [ReleaseScreen]
         */
        const val RELEASE_SCREEN = "ReleaseScreen"

        /**
         * *snackbarHostState* -> the host to launch the snackbar messages
         */
        val snackbarHostState = SnackbarHostState()

    }

    /**
     * Function to collect or instantiate the states of the screen
     *
     * No-any params required
     */
    @Composable
    protected open fun CollectStates() {

    }

    /**
     * Back navigation button
     *
     * @param modifier: the modifier to apply to the button
     * @param tint: the tint color to use for the icon
     */
    @Composable
    @NonRestartableComposable
    protected fun NavBackButton(
        modifier: Modifier = Modifier,
        tint: Color = Color.White
    ) {
        IconButton(
            modifier = modifier,
            onClick = { navigator.goBack() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = tint
            )
        }
    }

    /**
     * Loading screen to display when the data of an item is fetching
     *
     * @param item: the item to wait the loading of his data
     */
    @Composable
    @NonRestartableComposable
    protected fun LoadingData(
        item: State<EquinoxItem?>
    ) {
        AnimatedVisibility(
            visible = item.value == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptyListUI(
                icon = Icons.Default.Downloading,
                subText = string.loading_data
            )
        }
    }

    /**
     * Function invoked when the [ShowContent] composable has been resumed
     *
     * No-any params required
     */
    override fun onResume() {
        super.onResume()
        if (!isNotificationsFetchingEnable()) {
            startNotificationsFetching()
            fetchNotifications()
        }
    }

    /**
     * Function invoked when the [ShowContent] composable has been paused
     *
     * No-any params required
     */
    override fun onStop() {
        super.onStop()
        stopNotificationsFetching()
    }

}