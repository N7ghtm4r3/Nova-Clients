package com.tecknobit.nova.ui.screens.project

import androidx.compose.material3.SnackbarHostState
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.returnProjectInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProjectScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    private val _project = MutableStateFlow<Project?>(
        value = null
    )
    val project : StateFlow<Project?> = _project

    fun getProject(
        projectId : String
    ) {
        suspendRefresher()
        execRefreshingRoutine(
            currentContext = ProjectScreen::class.java,
            routine = {
                requester.sendRequest(
                    request = {
                        requester.getProject(
                            projectId = projectId
                        )
                    },
                    onSuccess = { response ->
                        _project.value = returnProjectInstance(response.getJSONObject(RESPONSE_MESSAGE_KEY))
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
        )
    }

}