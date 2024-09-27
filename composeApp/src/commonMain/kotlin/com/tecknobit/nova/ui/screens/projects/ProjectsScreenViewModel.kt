package com.tecknobit.nova.ui.screens.projects

import androidx.compose.material3.SnackbarHostState
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY
import com.tecknobit.novacore.records.project.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProjectsScreenViewModel(
    snackbarHostState: SnackbarHostState
): EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    private val _projects = MutableStateFlow(
        value = emptyList<Project>()
    )
    val projects: StateFlow<List<Project>> = _projects

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