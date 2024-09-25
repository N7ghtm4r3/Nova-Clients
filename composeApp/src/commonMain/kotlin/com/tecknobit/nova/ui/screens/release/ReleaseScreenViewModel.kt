package com.tecknobit.nova.ui.screens.release

import androidx.compose.material3.SnackbarHostState
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinoxcompose.helpers.session.setHasBeenDisconnectedValue
import com.tecknobit.equinoxcompose.helpers.session.setServerOfflineValue
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.records.release.Release
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReleaseScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

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

}