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
import com.tecknobit.novacore.records.project.Project
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
        project: Project?,
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
        if (project == null) {
            addProject(
                onSuccess = onSuccess
            )
        } else {
            editProject(
                project = project,
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
        project: Project,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.editProject(
                    project = project,
                    logoPic = if (logoPic.value.endsWith(project.logoUrl))
                        null
                    else
                        logoPic.value,
                    projectTitle = projectTitle.value,
                    members = membersAdded
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnackbarMessage(it) }
        )
    }

}