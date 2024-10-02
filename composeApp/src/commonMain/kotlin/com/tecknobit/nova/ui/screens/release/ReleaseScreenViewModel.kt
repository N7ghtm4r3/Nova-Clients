package com.tecknobit.nova.ui.screens.release

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.valueOf
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinox.Requester.Companion.RESPONSE_STATUS_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.getReportUrl
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.NovaInputValidator.areRejectionReasonsValid
import com.tecknobit.novacore.NovaInputValidator.isTagCommentValid
import com.tecknobit.novacore.records.release.Release
import com.tecknobit.novacore.records.release.Release.RELEASE_REPORT_PATH
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded
import com.tecknobit.novacore.records.release.events.RejectedTag
import com.tecknobit.novacore.records.release.events.ReleaseEvent
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ReleaseScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var uploadingAssetsComment: MutableState<String>

    lateinit var uploadingAssetsCommentError: MutableState<Boolean>

    lateinit var requestedToUpload: MutableState<Boolean>

    lateinit var requestedToDownload: MutableState<Boolean>

    lateinit var waitingAssetsManagement: MutableState<Boolean>

    lateinit var uploadingStatus: MutableState<StandardResponseCode?>

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
                if (!requestedToUpload.value && !requestedToDownload.value) {
                    requester.sendRequest(
                        request = {
                            requester.getRelease(
                                projectId = projectId,
                                releaseId = releaseId
                            )
                        },
                        onSuccess = { response ->
                            val jRelease = response.getJSONObject(RESPONSE_MESSAGE_KEY)
                            jRelease?.let { releaseData ->
                                _release.value = Release(releaseData)
                                setServerOfflineValue(false)
                            }
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
            }
        )
    }

    fun uploadAssets(
        projectId: String,
        releaseId: String,
    ) {
        if (!isTagCommentValid(uploadingAssetsComment.value)) {
            uploadingAssetsCommentError.value = true
            return
        }
        if (assetsToUpload.isNotEmpty()) {
            waitingAssetsManagement.value = true
            CoroutineScope(Dispatchers.Default).launch {
                requester.sendRequest(
                    request = {
                        requester.uploadAsset(
                            projectId = projectId,
                            releaseId = releaseId,
                            comment = uploadingAssetsComment.value,
                            assets = assetsToUpload
                        )
                    },
                    onResponse = { response ->
                        waitingAssetsManagement.value = false
                        uploadingStatus.value = valueOf(response.getString(RESPONSE_STATUS_KEY))
                    }
                )
            }
        }
    }

    fun resetUploadingInstances() {
        assetsToUpload.clear()
        uploadingStatus.value = null
        requestedToUpload.value = false
        uploadingAssetsComment.value = ""
        restartRefresher()
    }

    fun downloadTestAssets(
        assetsUploaded: List<AssetUploaded>,
        onSuccess: () -> Unit = {}
    ) {
        requestedToDownload.value = true
        CoroutineScope(Dispatchers.Default).launch {
            waitingAssetsManagement.value = true
            downloadAssetsUploaded(
                assetsUploaded = assetsUploaded
            )
        }.invokeOnCompletion {
            resetDownloadingInstances()
            onSuccess.invoke()
        }
    }

    fun resetDownloadingInstances() {
        requestedToDownload.value = false
        waitingAssetsManagement.value = false
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
            onSuccess = { response ->
                val reportUrl = getReportUrl(response.getString(RELEASE_REPORT_PATH))
                CoroutineScope(Dispatchers.Default).launch {
                    downloadReport(
                        report = reportUrl
                    )
                }
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