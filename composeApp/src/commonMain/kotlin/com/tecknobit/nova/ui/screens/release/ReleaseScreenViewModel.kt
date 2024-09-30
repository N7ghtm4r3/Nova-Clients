package com.tecknobit.nova.ui.screens.release

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.NovaInputValidator.areRejectionReasonsValid
import com.tecknobit.novacore.NovaInputValidator.isTagCommentValid
import com.tecknobit.novacore.records.release.Release
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import com.tecknobit.novacore.records.release.events.RejectedTag
import com.tecknobit.novacore.records.release.events.ReleaseEvent
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class ReleaseScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var requestedToUpload: MutableState<Boolean>

    lateinit var uploadingAssets: MutableState<Boolean>

    lateinit var uploadingStatus: MutableState<Boolean>

    lateinit var assetsToUpload: SnapshotStateList<File>

    lateinit var commentAsset: MutableState<Boolean>

    lateinit var isApproved: MutableState<Boolean>

    lateinit var reasons: MutableState<String>

    lateinit var reasonsError: MutableState<Boolean>

    lateinit var rejectedTags: SnapshotStateList<ReleaseTag>

    lateinit var rejectedTagDescription: MutableState<String>

    lateinit var rejectedTagDescriptionError: MutableState<Boolean>

    val closeAction = {
        isApproved.value = true
        reasons.value = ""
        reasonsError.value = false
        rejectedTags.clear()
        restartRefresher()
        commentAsset.value = false
    }

    private val _release = MutableStateFlow<Release?>(
        value = null
    )
    val release: StateFlow<Release?> = _release

    private var releaseDeleted = false

    fun getRelease(
        projectId: String,
        releaseId: String
    ) {
        suspendRefresher()
        execRefreshingRoutine(
            currentContext = ReleaseScreen::class.java,
            routine = {
                requester.sendRequest(
                    request = {
                        requester.getRelease(
                            projectId = projectId,
                            releaseId = releaseId
                        )
                    },
                    onSuccess = { response ->
                        _release.value = Release(
                            response.getJSONObject(
                                RESPONSE_MESSAGE_KEY
                            )
                        )
                        setServerOfflineValue(false)
                    },
                    onFailure = {
                        if (!releaseDeleted)
                            setHasBeenDisconnectedValue(true)
                    },
                    onConnectionError = {
                        setServerOfflineValue(true)
                    }
                )
            }
        )
    }

    fun uploadAssets(
        projectId: String,
        releaseId: String,
        comment: String,
        assets: List<File>
    ) {
        if (assets.isEmpty() || isTagCommentValid(comment));
        requester.sendRequest(
            request = {
                uploadingAssets.value = true
                requester.uploadAsset(
                    projectId = projectId,
                    releaseId = releaseId,
                    comment = comment,
                    assets = assets
                )
            },
            onSuccess = { uploadingStatus.value = true },
            onFailure = { uploadingStatus.value = false }
        )
    }

    fun createAndDownloadReport(
        projectId: String,
        releaseId: String
    ) {
        requester.sendRequest(
            request = {
                requester.createReportRelease(
                    projectId = projectId,
                    releaseId = releaseId
                )
            },
            onSuccess = {
                // TODO: TO SET
                /*
                val report = response.getString(RELEASE_REPORT_PATH)
                        resourcesCoroutine.launch {
                            runBlocking {
                                async {
                                    Desktop.getDesktop().open(
                                        APIRequest.downloadFile(
                                            getReportUrl(report),
                                            "${FileSystemView.getFileSystemView().homeDirectory}/Nova/" + report,
                                            true
                                        )
                                    )
                                }.await()
                                refreshItem()
                            }
                        }
                 */
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    fun deleteRelease(
        projectId: String,
        releaseId: String,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.deleteRelease(
                    projectId = projectId,
                    releaseId = releaseId
                )
            },
            onSuccess = {
                releaseDeleted = true
                onSuccess.invoke()
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    fun commentAssetsUploaded(
        projectId: String,
        releaseId: String,
        event: ReleaseEvent
    ) {
        if (isApproved.value) {
            requester.sendRequest(
                request = {
                    requester.approveAssets(
                        projectId = projectId,
                        releaseId = releaseId,
                        eventId = event.id
                    )
                },
                onSuccess = { closeAction() },
                onFailure = { response ->
                    closeAction()
                    showSnackbarMessage(response)
                }
            )
        } else {
            if (areRejectionReasonsValid(reasons.value)) {
                requester.sendRequest(
                    request = {
                        requester.rejectAssets(
                            projectId = projectId,
                            releaseId = releaseId,
                            eventId = event.id,
                            reasons = reasons.value,
                            tags = rejectedTags
                        )
                    },
                    onSuccess = { closeAction() },
                    onFailure = { response ->
                        closeAction()
                        showSnackbarMessage(response)
                    }
                )
            } else
                reasonsError.value = true
        }
    }

    fun fillRejectedTag(
        projectId: String,
        releaseId: String,
        event: ReleaseEvent,
        tag: RejectedTag,
        onSuccess: () -> Unit
    ) {
        if (!isTagCommentValid(rejectedTagDescription.value)) {
            rejectedTagDescriptionError.value = true
            return
        }
        requester.sendRequest(
            request = {
                requester.fillRejectedTag(
                    projectId = projectId,
                    releaseId = releaseId,
                    eventId = event.id,
                    tagId = tag.id,
                    comment = rejectedTagDescription.value
                )
            },
            onSuccess = {
                rejectedTagDescription.value = ""
                rejectedTagDescriptionError.value = false
                onSuccess.invoke()
            },
            onFailure = { response -> showSnackbarMessage(response) }
        )
    }

    fun promoteRelease(
        projectId: String,
        releaseId: String,
        newStatus: ReleaseStatus,
        onResponse: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.promoteRelease(
                    projectId = projectId,
                    releaseId = releaseId,
                    releaseStatus = newStatus
                )
            },
            onSuccess = { onResponse.invoke() },
            onFailure = { response ->
                onResponse.invoke()
                showSnackbarMessage(response)
            }
        )
    }

}