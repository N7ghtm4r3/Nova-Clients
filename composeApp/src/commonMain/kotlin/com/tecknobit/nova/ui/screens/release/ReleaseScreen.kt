@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeApi::class,
    ExperimentalResourceApi::class
)

package com.tecknobit.nova.ui.screens.release

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import com.tecknobit.equinoxcompose.components.EmptyListUI
import com.tecknobit.equinoxcompose.components.EquinoxAlertDialog
import com.tecknobit.equinoxcompose.helpers.session.ManagedContent
import com.tecknobit.nova.ReleaseStatusBadge
import com.tecknobit.nova.ReleaseTagBadge
import com.tecknobit.nova.getMessage
import com.tecknobit.nova.navigator
import com.tecknobit.nova.theme.BlueSchemeColors
import com.tecknobit.nova.theme.LightblueSchemeColors
import com.tecknobit.nova.theme.RedSchemeColors
import com.tecknobit.nova.theme.Typography
import com.tecknobit.nova.theme.VioletSchemeColors
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.theme.md_theme_light_primary
import com.tecknobit.nova.thinFontFamily
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.novacore.records.release.Release
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Approved
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Latest
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.New
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Verifying
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent
import com.tecknobit.novacore.records.release.events.RejectedTag
import com.tecknobit.novacore.records.release.events.ReleaseEvent
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag.Bug
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag.Issue
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag.LayoutChange
import com.tecknobit.novacore.records.release.events.ReleaseStandardEvent
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.comment
import nova.composeapp.generated.resources.confirm
import nova.composeapp.generated.resources.delete_release
import nova.composeapp.generated.resources.delete_release_alert_message
import nova.composeapp.generated.resources.dismiss
import nova.composeapp.generated.resources.new_asset_has_been_uploaded
import nova.composeapp.generated.resources.no_events_yet
import nova.composeapp.generated.resources.test
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

class ReleaseScreen(
    private val projectId: String,
    private val releaseId: String
) : NovaScreen() {

    companion object {

        private val viewModel = ReleaseScreenViewModel(
            snackbarHostState = snackbarHostState
        )

    }

    private lateinit var release: State<Release?>

    private var releaseStatus: ReleaseStatus = New

    /**
     * **deleteRelease** -> state used to display the delete release [DeleteRelease] UI
     */
    private lateinit var deleteRelease: MutableState<Boolean>

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        CollectStates()
        AnimatedVisibility(
            visible = release.value != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ManagedContent(
                viewModel = viewModel,
                content = {
                    Scaffold(
                        containerColor = gray_background,
                        topBar = {
                            LargeTopAppBar(
                                colors = TopAppBarDefaults.largeTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                navigationIcon = { NavBackButton() },
                                title = { ReleaseTitle() },
                                actions = { Actions() }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        floatingActionButton = { FAButton() }
                    ) { paddingValues ->
                        EventsSection(
                            paddingValues = paddingValues
                        )
                    }
                }
            )
        }
        LoadingData(
            item = release
        )
    }

    @Composable
    @NonRestartableComposable
    private fun ReleaseTitle() {
        Column {
            Text(
                text = release.value!!.project.name,
                color = Color.White,
                fontSize = 22.sp
            )
            Text(
                text = release.value!!.releaseVersion,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    @NonRestartableComposable
    private fun Actions() {
        IconButton(
            onClick = {
                viewModel.createAndDownloadReport(
                    projectId = projectId,
                    releaseId = releaseId
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = Color.White
            )
        }
        // TODO: HIDE IF TESTER
        IconButton(
            onClick = { deleteRelease.value = true }
        ) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = Color.White
            )
        }
        DeleteRelease()
    }

    @Composable
    private fun DeleteRelease() {
        EquinoxAlertDialog(
            show = deleteRelease,
            icon = Icons.Default.Warning,
            viewModel = viewModel,
            title = Res.string.delete_release,
            text = Res.string.delete_release_alert_message,
            confirmAction = {
                viewModel.deleteRelease(
                    projectId = projectId,
                    releaseId = releaseId,
                    onSuccess = {
                        deleteRelease.value = false
                        navigator.goBack()
                    }
                )
            },
            confirmText = Res.string.confirm,
            dismissText = Res.string.dismiss
        )
    }

    @Composable
    @NonRestartableComposable
    private fun FAButton() {
        val isReleaseApproved = releaseStatus == Approved
        if (releaseStatus != Latest && activeLocalSession.isVendor && releaseStatus != Verifying) {
            var uploadAsset by remember { mutableStateOf(false) }
            /*MultipleFilePicker(
                show = uploadAsset,
                fileExtensions = ALLOWED_ASSETS_TYPE
            ) { assets ->
                isUploading = true
                val closeAction = {
                    isUploading = false
                    refreshItem()
                }
                CoroutineScope(Dispatchers.Default).launch {
                    suspendRefresher()
                    val assetsFile = mutableListOf<File>()
                    if(!assets.isNullOrEmpty()) {
                        assets.forEach { asset ->
                            assetsFile.add(
                                File(asset.path)
                            )
                        }
                    }
                    requester.sendRequest(
                        request = {
                            requester.uploadAsset(
                                projectId = currentProject.value!!.id,
                                releaseId = currentRelease.value!!.id,
                                assets = assetsFile
                            )
                        },
                        onSuccess = {
                            closeAction.invoke()
                        },
                        onFailure = { response ->
                            snackbarLauncher.showSnack(
                                message = response.getString(RESPONSE_MESSAGE_KEY)
                            )
                            closeAction.invoke()
                        }
                    )
                }
                uploadAsset = false
            }*/
            FloatingActionButton(
                containerColor = md_theme_light_primary,
                onClick = {
                    /*if(!isUploading) {
                        if(isReleaseApproved) {
                            suspendRefresher()
                            showPromoteRelease.value = true
                        } else
                            uploadAsset = true
                    }*/
                }
            ) {
                /*if(isUploading) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                } else {*/
                Icon(
                    imageVector = if (isReleaseApproved)
                        Icons.Default.Verified
                    else
                        Icons.Default.Upload,
                    contentDescription = null,
                    tint = Color.White
                )
                //}
            }
            //PromoteRelease()
        }
    }

    @Composable
    @NonRestartableComposable
    private fun EventsSection(
        paddingValues: PaddingValues
    ) {
        val events = release.value!!.releaseEvents
        if (events.isNotEmpty()) {
            JetLimeColumn(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() + 25.dp,
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    ),
                itemsList = ItemsList(events),
                style = JetLimeDefaults.columnStyle(
                    contentDistance = 24.dp,
                    lineThickness = 3.dp
                ),
                key = { _, item -> item.id }
            ) { _, event, position ->
                ReleaseEvent(
                    event = event,
                    position = position
                )
            }
        } else {
            EmptyListUI(
                icon = Icons.Default.EventBusy,
                subText = Res.string.no_events_yet
            )
        }
    }

    @Composable
    @NonRestartableComposable
    private fun ReleaseEvent(
        event: ReleaseEvent,
        position: EventPosition
    ) {
        val isAssetUploadingEvent = event is AssetUploadingEvent
        JetLimeExtendedEvent(
            style = JetLimeEventDefaults.eventStyle(
                position = position,
                pointRadius = 11.5.dp,
                pointStrokeWidth = if (isAssetUploadingEvent)
                    6.dp
                else
                    1.5.dp
            ),
            additionalContent = {
                Column(
                    modifier = Modifier
                        .width(105.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (event is ReleaseStandardEvent) {
                        ReleaseStatusBadge(
                            releaseStatus = event.status,
                            paddingStart = 0.dp,
                        )
                    }
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column {
                    Text(
                        text = event.releaseEventDate,
                        fontFamily = thinFontFamily,
                    )
                    if (event !is RejectedReleaseEvent) {
                        val message = if (isAssetUploadingEvent)
                            Res.string.new_asset_has_been_uploaded
                        else
                            (event as ReleaseStandardEvent).getMessage()
                        Text(
                            text = stringResource(message),
                        )
                        if ((isAssetUploadingEvent && ((releaseStatus != Approved) &&
                                    (releaseStatus != Latest)))
                        ) {
                            if (!(event as AssetUploadingEvent).isCommented) {
                                val showCommentAsset = remember { mutableStateOf(false) }
                                val isApproved = remember { mutableStateOf(true) }
                                val reasons = remember { mutableStateOf("") }
                                val reasonsErrorMessage = remember { mutableStateOf("") }
                                val isError = remember { mutableStateOf(false) }
                                val rejectedTags = remember {
                                    mutableListOf<ReleaseTag>()
                                }
                                val closeAction = {
                                    isApproved.value = true
                                    reasons.value = ""
                                    isError.value = false
                                    reasonsErrorMessage.value = ""
                                    showCommentAsset.value = false
                                    rejectedTags.clear()
                                }
                                /*NovaAlertDialog(
                                    show = showCommentAsset,
                                    icon = Icons.AutoMirrored.Filled.Comment,
                                    onDismissAction = closeAction,
                                    title = Res.string.comment_the_asset,
                                    message = commentReleaseMessage(
                                        isApproved = isApproved,
                                        reasons = reasons,
                                        isError = isError,
                                        reasonsErrorMessage = reasonsErrorMessage,
                                        rejectedTags = rejectedTags
                                    ),
                                    dismissAction = closeAction,
                                    confirmAction = {
                                        if(isApproved.value) {
                                            requester.sendRequest(
                                                request = {
                                                    requester.approveAssets(
                                                        projectId = currentProject.value!!.id!!,
                                                        releaseId = currentRelease.value!!.id,
                                                        eventId = event.id
                                                    )
                                                },
                                                onSuccess = {
                                                    closeAction()
                                                },
                                                onFailure = { response ->
                                                    closeAction()
                                                    snackbarLauncher.showSnack(
                                                        message = response
                                                            .getString(RESPONSE_MESSAGE_KEY)
                                                    )
                                                }
                                            )
                                        } else {
                                            if(areRejectionReasonsValid(reasons.value)) {
                                                requester.sendRequest(
                                                    request = {
                                                        requester.rejectAssets(
                                                            projectId = currentProject.value!!.id!!,
                                                            releaseId = currentRelease.value!!.id,
                                                            eventId = event.id,
                                                            reasons = reasons.value,
                                                            tags = rejectedTags
                                                        )
                                                    },
                                                    onSuccess = {
                                                        closeAction()
                                                    },
                                                    onFailure = { response ->
                                                        closeAction()
                                                        snackbarLauncher.showSnack(
                                                            message = response
                                                                .getString(RESPONSE_MESSAGE_KEY)
                                                        )
                                                    }
                                                )
                                            } else {
                                                setErrorMessage(
                                                    errorMessage = reasonsErrorMessage,
                                                    errorMessageKey = Res.string.wrong_reasons,
                                                    error = isError
                                                )
                                            }
                                        }
                                    }
                                )*/
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            event.assetsUploaded.forEach { asset ->
                                                // TODO: TO SET
                                                /*downloadAndOpenAsset(
                                                    asset = asset
                                                )*/
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.test)
                                        )
                                    }
                                    if (activeLocalSession.isCustomer) {
                                        Button(
                                            onClick = { showCommentAsset.value = true }
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.comment)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Column {
                            Text(
                                text = event.reasons,
                                textAlign = TextAlign.Justify
                            )
                            LazyHorizontalGrid(
                                modifier = Modifier
                                    .requiredHeightIn(
                                        min = 35.dp,
                                        max = 70.dp
                                    ),
                                contentPadding = PaddingValues(
                                    top = 5.dp
                                ),
                                rows = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                items(
                                    key = { tag -> tag.tag.name },
                                    items = event.tags
                                ) { tag ->
                                    val showAlert = remember {
                                        mutableStateOf(false)
                                    }
                                    ReleaseTagBadge(
                                        tag = tag,
                                        isLastEvent = release.value!!.isLastEvent(event),
                                        onClick = { showAlert.value = true }
                                    )
                                    TagInformation(
                                        show = showAlert,
                                        event = event,
                                        tag = tag,
                                        date = event.releaseEventDate
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to display a [EquinoxAlertDialog] with the comment of a [RejectedTag]
     *
     * @param show: whether show or not the alert
     * @param event: the event where the tag is placed
     * @param tag: the tag used to display the alert
     * @param date: the date when the tag has been commented
     */
    @Composable
    @NonRestartableComposable
    private fun TagInformation(
        show: MutableState<Boolean>,
        event: RejectedReleaseEvent,
        tag: RejectedTag,
        date: String
    ) {
        MaterialTheme(
            colorScheme = when (tag.tag) {
                Bug -> RedSchemeColors
                Issue -> VioletSchemeColors
                LayoutChange -> LightblueSchemeColors
                else -> BlueSchemeColors
            },
            typography = Typography,
        ) {
            /*if(show.value) {
                suspendRefresher()
                val isInputMode = tag.comment == null || tag.comment.isEmpty()
                val isInputButton = @Composable {
                    TextButton(
                        onClick = { show.value = false }
                    ) {
                        Text(
                            text = stringResource(Res.string.dismiss)
                        )
                    }
                }
                val dismissButton = if(isInputMode)
                    isInputButton
                else
                    null
                val description = remember { mutableStateOf("") }
                val isError = remember { mutableStateOf(false) }
                val descriptionErrorMessage = remember { mutableStateOf("") }
                AlertDialog(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null
                        )
                    },
                    onDismissRequest = { show.value = false },
                    title = {
                        Text(
                            text = tag.tag.name,
                            textAlign = TextAlign.Start
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = date,
                                fontFamily = thinFontFamily,
                            )
                            if(!isInputMode) {
                                Text(
                                    text = tag.comment,
                                    textAlign = TextAlign.Justify
                                )
                            } else {
                                NovaTextField(
                                    value = description,
                                    onValueChange = {
                                        isError.value = !isTagCommentValid(it) &&
                                                description.value.isNotEmpty()
                                        checkToSetErrorMessage(
                                            errorMessage = descriptionErrorMessage,
                                            errorMessageKey = Res.string.wrong_description,
                                            error = isError
                                        )
                                        description.value = it
                                    },
                                    label = Res.string.description,
                                    errorMessage = descriptionErrorMessage,
                                    isError = isError
                                )
                            }
                        }
                    },
                    dismissButton = dismissButton,
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if(isInputMode) {
                                    val closeAction = {
                                        show.value = false
                                        refreshItem()
                                    }
                                    if(isTagCommentValid(description.value)) {
                                        requester.sendRequest(
                                            request = {
                                                requester.fillRejectedTag(
                                                    projectId = currentProject.value!!.id!!,
                                                    releaseId = currentRelease.value!!.id,
                                                    eventId = event.id,
                                                    tagId = tag.id,
                                                    comment = description.value
                                                )
                                            },
                                            onSuccess = {
                                                closeAction.invoke()
                                            },
                                            onFailure = { response ->
                                                closeAction.invoke()
                                                snackbarLauncher.showSnack(
                                                    message = response.getString(RESPONSE_MESSAGE_KEY)
                                                )
                                            }
                                        )
                                    } else {
                                        setErrorMessage(
                                            errorMessage = descriptionErrorMessage,
                                            errorMessageKey = Res.string.wrong_description,
                                            error = isError
                                        )
                                    }
                                } else
                                    show.value = false
                            }
                        ) {
                            val buttonText = if(isInputMode)
                                Res.string.confirm
                            else
                                Res.string.close
                            Text(
                                text = stringResource(buttonText)
                            )
                        }
                    }
                )
            } else
                refreshItem()*/
        }
    }

    @Composable
    override fun CollectStates() {
        super.CollectStates()
        release = viewModel.release.collectAsState()
        release.value?.let { release: Release ->
            releaseStatus = release.status
        }
        deleteRelease = remember { mutableStateOf(false) }
    }

    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(this::class.java)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getRelease(
            projectId = projectId,
            releaseId = releaseId
        )
    }

    /**
     * Function invoked when the [ShowContent] composable has been resumed
     *
     * No-any params required
     */
    override fun onResume() {
        super.onResume()
        if (!deleteRelease.value)
            viewModel.restartRefresher()
    }

    /**
     * Function invoked when the [ShowContent] composable has been paused
     *
     * No-any params required
     */
    override fun onPause() {
        super.onPause()
        viewModel.suspendRefresher()
    }

    /**
     * Function invoked when the [ShowContent] composable has been stopped
     *
     * No-any params required
     */
    override fun onStop() {
        super.onStop()
        viewModel.suspendRefresher()
    }

}