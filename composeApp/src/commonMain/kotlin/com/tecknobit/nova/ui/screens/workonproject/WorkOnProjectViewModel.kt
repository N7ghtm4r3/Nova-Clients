package com.tecknobit.nova.ui.screens.workonproject

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.theme.tagstheme.bug.md_theme_light_primary
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.NovaInputValidator.isProjectNameValid
import com.tecknobit.novacore.records.NovaUser
import com.tecknobit.novacore.records.NovaUser.returnUsersList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkOnProjectViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    private val _potentialMembers = MutableStateFlow(
        value = emptyList<NovaUser>()
    )
    val potentialMembers: StateFlow<List<NovaUser>> = _potentialMembers

    lateinit var membersAdded: SnapshotStateList<String>

    lateinit var logoPic: MutableState<String>

    lateinit var logoPicBordersColor: MutableState<Color>

    lateinit var projectTitle: MutableState<String>

    lateinit var projectTitleError: MutableState<Boolean>

    fun getPotentialMembers() {
        execRefreshingRoutine(
            currentContext = WorkOnProject::class.java,
            routine = {
                requester.sendRequest(
                    request = { requester.getPotentialMembers() },
                    onSuccess = { response ->
                        _potentialMembers.value = returnUsersList(
                            (response.getJSONArray(
                                RESPONSE_MESSAGE_KEY
                            ))
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
        )
    }

    fun workOnProject(
        projectId: String?,
        onSuccess: () -> Unit
    ) {
        if (logoPic.value.isEmpty()) {
            logoPicBordersColor.value = md_theme_light_primary
            return
        }
        if (!isProjectNameValid(projectTitle.value)) {
            projectTitleError.value = true
            return
        }
        if (projectId == null) {
            addProject(
                onSuccess = onSuccess
            )
        } else {
            editProject(
                projectId = projectId,
                onSuccess = onSuccess
            )
        }
    }

    private fun addProject(
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.addProject(
                    logoPic = logoPic.value,
                    projectTitle = projectTitle.value,
                    members = membersAdded
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    private fun editProject(
        projectId: String,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.editProject(
                    projectId = projectId,
                    logoPic = logoPic.value,
                    projectTitle = projectTitle.value,
                    members = membersAdded
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnackbarMessage(it) }
        )
    }

}