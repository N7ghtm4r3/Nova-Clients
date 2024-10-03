package com.tecknobit.nova.ui.screens.project

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinoxcompose.components.EquinoxAlertDialog
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.NovaInputValidator.areReleaseNotesValid
import com.tecknobit.novacore.NovaInputValidator.isReleaseVersionValid
import com.tecknobit.novacore.records.NovaUser
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.returnProjectInstance
import com.tecknobit.novacore.records.release.Release
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The **ProjectScreenViewModel** class is the support class used by the [ProjectScreen] to execute
 * the requests to refresh and work on a project
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 * @see EquinoxViewModel
 *
 */
class ProjectScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **addRelease** -> state used to display the [EquinoxAlertDialog] to add a new release
     */
    lateinit var addRelease: MutableState<Boolean>

    /**
     * **releaseToEdit** -> the release to edit, if requested
     */
    lateinit var releaseToEdit: MutableState<Release?>

    /**
     * **releaseVersion** -> the version of the release
     */
    lateinit var releaseVersion: MutableState<String>

    /**
     * **releaseVersionError** -> whether the [releaseVersion] field is not valid
     */
    lateinit var releaseVersionError: MutableState<Boolean>

    /**
     * **releaseNotes** -> the notes of the release
     */
    lateinit var releaseNotes: MutableState<String>

    /**
     * **releaseNotesError** -> whether the [releaseNotes] field is not valid
     */
    lateinit var releaseNotesError: MutableState<Boolean>

    /**
     * **_project** -> the project currently shown
     */
    private val _project = MutableStateFlow<Project?>(
        value = null
    )
    val project : StateFlow<Project?> = _project

    /**
     * **projectDeleted** -> whether the project has been deleted
     */
    private var projectDeleted = false

    /**
     * Function to refresh the [_project]
     *
     * @param projectId: the identifier of the project
     */
    fun getProject(
        projectId : String
    ) {
        suspendRefresher()
        execRefreshingRoutine(
            currentContext = ProjectScreen::class.java,
            routine = {
                if (releaseToEdit.value == null && !addRelease.value) {
                    requester.sendRequest(
                        request = {
                            requester.getProject(
                                projectId = projectId
                            )
                        },
                        onSuccess = { response ->
                            val jProject = response.getJSONObject(RESPONSE_MESSAGE_KEY)
                            jProject?.let { projectData ->
                                _project.value = returnProjectInstance(projectData)
                                setServerOfflineValue(false)
                            }
                        },
                        onFailure = {
                            if (!projectDeleted)
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

    /**
     * Function to execute the request to mark a member as [NovaUser.Role.Tester]
     *
     * @param member: the member to mark as tester
     * @param onSuccess: the action to execute when the request has been successful
     */
    fun markAsTester(
        member: NovaUser,
        onSuccess: () -> Unit
    ) {
        val memberId = member.id
        requester.sendRequest(
            request = {
                requester.markAsTester(
                    projectId = _project.value!!.id,
                    memberId = memberId
                )
            },
            onSuccess = {
                onSuccess.invoke()
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    /**
     * Function to execute the request to remove a member
     *
     * @param member: the member to remove
     */
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

    /**
     * Function to execute the request to delete or leave the project
     *
     * @param amITheProjectAuthor: whether the user who requested the action is the author of the project
     * it will be deleted
     * @param onSuccess: the action to execute when the request has been successful
     */
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

    /**
     * Function to add a new release to the current [_project]
     *
     * No-any params required
     */
    fun addRelease() {
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
                addRelease.value = false
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    /**
     * Function to edit an exiting release
     *
     * @param release: the release to edit
     * @param releaseVersion: the version of the release
     * @param releaseVersionError: whether the [releaseVersion] field is not valid
     * @param releaseNotes: the notes of the release
     * @param releaseNotesError: whether the [releaseNotes] field is not valid
     * @param onSuccess: the action to execute when the request has been successful
     *
     */
    fun editRelease(
        release: Release,
        releaseVersion: MutableState<String>,
        releaseVersionError: MutableState<Boolean>,
        releaseNotes: MutableState<String>,
        releaseNotesError: MutableState<Boolean>,
        onSuccess: () -> Unit
    ) {
        validateReleasePayload(
            releaseVersion = releaseVersion,
            releaseVersionError = releaseVersionError,
            releaseNotes = releaseNotes,
            releaseNotesError = releaseNotesError
        )
        requester.sendRequest(
            request = {
                requester.editRelease(
                    projectId = _project.value!!.id,
                    releaseId = release.id,
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

    /**
     * Function to validate the payload about the release data
     *
     * No-any params required
     */
    @Wrapper
    private fun validateReleasePayload() {
        validateReleasePayload(
            releaseVersion = releaseVersion,
            releaseVersionError = releaseVersionError,
            releaseNotes = releaseNotes,
            releaseNotesError = releaseNotesError
        )
    }

    /**
     * Function to validate the payload about the release data
     *
     * @param releaseVersion: the version of the release
     * @param releaseVersionError: whether the [releaseVersion] field is not valid
     * @param releaseNotes: the notes of the release
     * @param releaseNotesError: whether the [releaseNotes] field is not valid
     */
    private fun validateReleasePayload(
        releaseVersion: MutableState<String>,
        releaseVersionError: MutableState<Boolean>,
        releaseNotes: MutableState<String>,
        releaseNotesError: MutableState<Boolean>
    ) {
        if (!isReleaseVersionValid(releaseVersion.value)) {
            releaseVersionError.value = true
            return
        }
        if (!areReleaseNotesValid(releaseNotes.value)) {
            releaseNotesError.value = true
            return
        }
    }

}