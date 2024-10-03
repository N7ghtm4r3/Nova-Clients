package com.tecknobit.nova.ui.screens.projects

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY
import com.tecknobit.novacore.records.project.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The **ProjectsScreenViewModel** class is the support class used by the [ProjectsScreen] to execute
 * the refreshing routines to update the projects data of the user
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 * @see EquinoxViewModel
 *
 */
class ProjectsScreenViewModel(
    snackbarHostState: SnackbarHostState
): EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **_projects** -> the projects list of the user
     */
    private val _projects = MutableStateFlow(
        value = emptyList<Project>()
    )
    val projects: StateFlow<List<Project>> = _projects

    /**
     * Function to refresh the [_projects] list
     *
     * No-any params required
     */
    fun getProjects() {
        suspendRefresher()
        execRefreshingRoutine(
            currentContext = ProjectsScreen::class.java,
            routine = {
                if (activeLocalSession.isHostSet) {
                    requester.sendRequest(
                        request = { requester.listProjects() },
                        onSuccess = { response ->
                            _projects.value = Project.returnProjectsList(
                                response.getJSONArray(
                                    PROJECTS_KEY
                                )
                            )
                            setServerOfflineValue(false)
                        },
                        onFailure = {
                            setHasBeenDisconnectedValue(true)
                        },
                        onConnectionError = {
                            setServerOfflineValue(true)
                        }
                    )
                }
            }
        )
    }

}