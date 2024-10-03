package com.tecknobit.nova.ui.screens.release

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.valueOf
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinox.Requester.Companion.RESPONSE_STATUS_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.components.getReportUrl
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

/**
 * The **ReleaseScreenViewModel** class is the support class used by the [ReleaseScreen] to execute
 * the requests to refresh and work on a release
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 * @see EquinoxViewModel
 *
 */
class ReleaseScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **uploadingAssetsComment** -> the comment about the assets that have to be uploaded
     */
    lateinit var uploadingAssetsComment: MutableState<String>

    /**
     * **uploadingAssetsCommentError** -> whether the [uploadingAssetsComment] field is not valid
     */
    lateinit var uploadingAssetsCommentError: MutableState<Boolean>

    /**
     * **requestedToUpload** -> whether the user requested to upload new assets
     */
    lateinit var requestedToUpload: MutableState<Boolean>

    /**
     * **requestedToDownload** -> whether the user requested to download the test assets
     */
    lateinit var requestedToDownload: MutableState<Boolean>

    /**
     * **waitingAssetsManagement** -> whether the [requestedToUpload] or [requestedToDownload] have
     * been triggered
     */
    lateinit var waitingAssetsManagement: MutableState<Boolean>

    /**
     * **uploadingStatus** -> status about the uploading
     */
    lateinit var uploadingStatus: MutableState<StandardResponseCode?>

    /**
     * **assetsToUpload** -> the assets selected for the download
     */
    lateinit var assetsToUpload: SnapshotStateList<File>

    /**
     * **commentAsset** -> whether the assets has been commented
     */
    lateinit var commentAsset: MutableState<Boolean>

    /**
     * **isApproved** -> whether the assets has been approved
     */
    lateinit var isApproved: MutableState<Boolean>

    /**
     * **reasons** -> the reasons of a rejection of the assets
     */
    lateinit var reasons: MutableState<String>

    /**
     * **reasonsError** -> whether the [reasons] field is not valid
     */
    lateinit var reasonsError: MutableState<Boolean>

    /**
     * **rejectedTags** -> the rejection tags selected
     */
    lateinit var rejectedTags: SnapshotStateList<ReleaseTag>

    /**
     * **rejectedTagDescription** -> the description for a rejection tag
     */
    lateinit var rejectedTagDescription: MutableState<String>

    /**
     * **rejectedTagDescriptionError** -> whether the [rejectedTagDescription] field is not valid
     */
    lateinit var rejectedTagDescriptionError: MutableState<Boolean>

    /**
     * **closeAction** -> action to invoke when the dialog to approve or reject the assets has been
     * closed
     */
    val closeAction = {
        isApproved.value = true
        reasons.value = ""
        reasonsError.value = false
        rejectedTags.clear()
        restartRefresher()
        commentAsset.value = false
    }

    /**
     * **_release** -> the release currently shown
     */
    private val _release = MutableStateFlow<Release?>(
        value = null
    )
    val release: StateFlow<Release?> = _release

    /**
     * **releaseDeleted** -> whether the release has been deleted
     */
    private var releaseDeleted = false

    /**
     * Function to refresh the [_release]
     *
     * @param projectId: the identifier of the project
     * @param releaseId: the identifier of the release
     */
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

    /**
     * Function to upload new asset for a [_release]
     *
     * @param projectId: the identifier of the project
     * @param releaseId: the identifier of the release
     */
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

    /**
     * Function to reset the instance used during the [uploadAssets] phase
     *
     * No-any params required
     */
    fun resetUploadingInstances() {
        assetsToUpload.clear()
        uploadingStatus.value = null
        requestedToUpload.value = false
        uploadingAssetsComment.value = ""
        restartRefresher()
    }

    /**
     * Function to download the test assets
     *
     * @param assetsUploaded: the assets uploaded to download
     * @param onSuccess: the action to execute when the download terminated
     */
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

    /**
     * Function to reset the instance used during the [downloadTestAssets] phase
     *
     * No-any params required
     */
    fun resetDownloadingInstances() {
        requestedToDownload.value = false
        waitingAssetsManagement.value = false
    }

    /**
     * Function to execute the request to create a report for the current [_release]
     *
     * @param projectId: the identifier of the project
     * @param releaseId: the identifier of the release
     */
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

    /**
     * Function to execute the request to delete the current [_release]
     *
     * @param projectId: the identifier of the project
     * @param releaseId: the identifier of the release
     * @param onSuccess: the action to execute when the request has been successful
     */
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

    /**
     * Function to execute the request to comment assets uploaded in an event
     *
     * @param projectId: the identifier of the project
     * @param releaseId: the identifier of the release
     * @param event: the event where the assets are attached
     */
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

    /**
     * Function to execute the request to fill a [ReleaseTag] of a release rejection
     *
     * @param projectId: the identifier of the project
     * @param releaseId: the identifier of the release
     * @param event: the event where the assets are attached
     * @param tag: the tag to fill
     * @param onSuccess: the action to execute when the request has been successful
     */
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

    /**
     * Function to execute the promote the current relase
     *
     * @param projectId: the identifier of the project
     * @param releaseId: the identifier of the release
     * @param newStatus: the new status to apply to the [_release]
     * @param onResponse: the action to execute when received a response
     */
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