package com.tecknobit.nova.ui.screens.project

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.NovaInputValidator.areReleaseNotesValid
import com.tecknobit.novacore.NovaInputValidator.isReleaseVersionValid
import com.tecknobit.novacore.records.NovaUser
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.returnProjectInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProjectScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var releaseVersion: MutableState<String>

    lateinit var releaseVersionError: MutableState<Boolean>

    lateinit var releaseNotes: MutableState<String>

    lateinit var releaseNotesError: MutableState<Boolean>

    //lateinit var releaseVersionErrorMessage: MutableState<String>
    //lateinit var releaseNotesErrorMessage = remember { mutableStateOf("") }

    private val _project = MutableStateFlow<Project?>(
        value = null
    )
    val project : StateFlow<Project?> = _project

    private var projectDeleted = false

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
                        if(!projectDeleted)
                            setHasBeenDisconnectedValue(true)
                    },
                    onConnectionError = {
                        setServerOfflineValue(true)
                    }
                )
            }
        )
    }

    fun removeMember(
        member: NovaUser
    ) {
        requester.sendRequest(
            request = {
                requester.removeMember(
                    projectId = _project.value!!.id,
                    memberId = member.id
                )
            },
            onSuccess = {},
            onFailure = { showSnackbarMessage(it) }
        )
    }

    fun workOnProject(
        amITheProjectAuthor: Boolean,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                if(amITheProjectAuthor) {
                    requester.deleteProject(
                        projectId = _project.value!!.id
                    )
                } else {
                    requester.leaveProject(
                        projectId = _project.value!!.id
                    )
                }
            },
            onSuccess = {
                projectDeleted = true
                onSuccess.invoke()
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    fun addRelease(
        onSuccess: () -> Unit
    ) {
        validateReleasePayload()
        requester.sendRequest(
            request = {
                requester.addRelease(
                    projectId = _project.value!!.id,
                    releaseVersion = releaseVersion.value,
                    releaseNotes = releaseNotes.value
                )
            },
            onSuccess = {
                releaseVersion.value = ""
                releaseVersionError.value = false
                releaseNotes.value = ""
                releaseNotesError.value = false
                onSuccess.invoke()
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    private fun validateReleasePayload() {
        if (!isReleaseVersionValid(releaseVersion.value)) {
            releaseVersionError.value = true
            return
        }
        if (!areReleaseNotesValid(releaseNotes.value)) {
            releaseVersionError.value = true
            return
        }
    }

}