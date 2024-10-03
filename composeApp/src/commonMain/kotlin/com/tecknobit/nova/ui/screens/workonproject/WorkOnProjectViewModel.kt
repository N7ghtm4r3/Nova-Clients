package com.tecknobit.nova.ui.screens.workonproject

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_primary
import com.tecknobit.novacore.NovaInputValidator.isProjectNameValid
import com.tecknobit.novacore.records.NovaUser
import com.tecknobit.novacore.records.NovaUser.returnUsersList
import com.tecknobit.novacore.records.project.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The **WorkOnProjectViewModel** class is the support class used by the [WorkOnProject] to execute
 * the requests to create or edit a [Project]
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 * @see EquinoxViewModel
 *
 */
class WorkOnProjectViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * *_potentialMembers* -> the list of the potential members to add to the project
     */
    private val _potentialMembers = MutableStateFlow(
        value = emptyList<NovaUser>()
    )
    val potentialMembers: StateFlow<List<NovaUser>> = _potentialMembers

    /**
     * *membersAdded* -> the list of identifiers of the added members
     */
    lateinit var membersAdded: SnapshotStateList<String>

    /**
     * *logoPic* -> the logo picture of the project
     */
    lateinit var logoPic: MutableState<String>

    /**
     * *logoPicBordersColor* -> the color used for the borders of the [logoPic] to warn about an error
     * during the selection
     */
    lateinit var logoPicBordersColor: MutableState<Color>

    /**
     * *projectTitle* -> the state used to contain the title of the project
     */
    lateinit var projectTitle: MutableState<String>

    /**
     * **projectTitleError** -> whether the [projectTitle] field is not valid
     */
    lateinit var projectTitleError: MutableState<Boolean>

    /**
     * Function to refresh the [_potentialMembers] list
     *
     * No-any params required
     */
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

    /**
     * Wrapper function to execute the add or the edit request
     *
     * @param project: the project to edit if passed
     * @param onSuccess: the action to execute the request has been successful
     */
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

    /**
     * Function to execute the add project request
     *
     * @param onSuccess: the action to execute the request has been successful
     */
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

    /**
     * Function to execute the edit project request
     *
     * @param project: the project to edit if passed
     * @param onSuccess: the action to execute the request has been successful
     */
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