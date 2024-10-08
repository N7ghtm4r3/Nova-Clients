@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeApi::class,
    ExperimentalResourceApi::class, ExperimentalRichTextApi::class
)

package com.tecknobit.nova.ui.screens.release

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.TestOnly
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material.RichText
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL
import com.tecknobit.equinoxcompose.components.EmptyListUI
import com.tecknobit.equinoxcompose.components.EquinoxAlertDialog
import com.tecknobit.equinoxcompose.components.EquinoxTextField
import com.tecknobit.equinoxcompose.components.ErrorUI
import com.tecknobit.equinoxcompose.helpers.session.ManagedContent
import com.tecknobit.nova.navigator
import com.tecknobit.nova.thinFontFamily
import com.tecknobit.nova.ui.components.AssetsDialog
import com.tecknobit.nova.ui.components.AssetsToDownload
import com.tecknobit.nova.ui.components.AssetsToUpload
import com.tecknobit.nova.ui.components.DialogsActions
import com.tecknobit.nova.ui.components.ReleaseStatusBadge
import com.tecknobit.nova.ui.components.ReleaseTagBadge
import com.tecknobit.nova.ui.components.createColor
import com.tecknobit.nova.ui.components.getMessage
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.theme.BlueSchemeColors
import com.tecknobit.nova.ui.theme.LightblueSchemeColors
import com.tecknobit.nova.ui.theme.RedSchemeColors
import com.tecknobit.nova.ui.theme.TesterThemeColor
import com.tecknobit.nova.ui.theme.Typography
import com.tecknobit.nova.ui.theme.VioletSchemeColors
import com.tecknobit.nova.ui.theme.gray_background
import com.tecknobit.nova.ui.theme.md_theme_light_primary
import com.tecknobit.novacore.NovaInputValidator.areRejectionReasonsValid
import com.tecknobit.novacore.NovaInputValidator.isTagCommentValid
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.release.Release
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Alpha
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Approved
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Beta
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Latest
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.New
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Rejected
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Verifying
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent
import com.tecknobit.novacore.records.release.events.RejectedTag
import com.tecknobit.novacore.records.release.events.ReleaseEvent
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag.Bug
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag.Issue
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag.LayoutChange
import com.tecknobit.novacore.records.release.events.ReleaseStandardEvent
import io.github.vinceglb.filekit.compose.PickerResultLauncher
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.approve
import nova.composeapp.generated.resources.assets_summary
import nova.composeapp.generated.resources.close
import nova.composeapp.generated.resources.comment
import nova.composeapp.generated.resources.comment_the_asset
import nova.composeapp.generated.resources.confirm
import nova.composeapp.generated.resources.delete_release
import nova.composeapp.generated.resources.delete_release_alert_message
import nova.composeapp.generated.resources.description
import nova.composeapp.generated.resources.dismiss
import nova.composeapp.generated.resources.downloading_assets
import nova.composeapp.generated.resources.new_asset_has_been_uploaded
import nova.composeapp.generated.resources.no_events_yet
import nova.composeapp.generated.resources.promote_alpha_release_alert_message
import nova.composeapp.generated.resources.promote_beta_release_alert_message
import nova.composeapp.generated.resources.promote_latest_release_alert_message
import nova.composeapp.generated.resources.promote_release
import nova.composeapp.generated.resources.promote_release_as_beta
import nova.composeapp.generated.resources.promote_release_as_latest
import nova.composeapp.generated.resources.reasons
import nova.composeapp.generated.resources.reject
import nova.composeapp.generated.resources.retry
import nova.composeapp.generated.resources.select_assets_to_download
import nova.composeapp.generated.resources.tags
import nova.composeapp.generated.resources.test
import nova.composeapp.generated.resources.uploading_assets
import nova.composeapp.generated.resources.uploading_assets_failed
import nova.composeapp.generated.resources.uploading_assets_successful
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.io.File

/**
 * The [ReleaseScreen] class is used to retrieve and display the data of a release
 *
 * @param projectId: the identifier of the project
 * @param releaseId: the identifier of the release
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxScreen
 * @see NovaScreen
 */
class ReleaseScreen(
    private val projectId: String,
    private val releaseId: String
) : NovaScreen() {

    companion object {

        /**
         * *viewModel* -> the support view model to manage the requests to the backend
         */
        private val viewModel = ReleaseScreenViewModel(
            snackbarHostState = snackbarHostState
        )

    }

    /**
     * *launcher* -> the launcher used to pick the assets to upload
     */
    private lateinit var launcher: PickerResultLauncher

    /**
     * **release** -> the release currently shown
     */
    private lateinit var release: State<Release?>

    /**
     * *amITester* -> whether the [activeLocalSession] is a [NovaUser.Role.Tester]
     */
    private lateinit var amITester: MutableState<Boolean>

    /**
     * *releaseProject* -> the project where the release is attached
     */
    private lateinit var releaseProject: Project

    /**
     * *releaseStatus* -> the status of the release
     */
    private var releaseStatus: ReleaseStatus = New

    /**
     * **deleteRelease** -> state used to display the delete release [DeleteRelease] UI
     */
    private lateinit var deleteRelease: MutableState<Boolean>

    /**
     * **promoteRelease** -> state used to display the [PromoteRelease] UI
     */
    private lateinit var promoteRelease: MutableState<Boolean>

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

    /**
     * The section where is displayed the title of the release
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun ReleaseTitle() {
        Column {
            Text(
                text = releaseProject.name,
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

    /**
     * The section where are displayed the actions available for the [release], these actions change
     * in base of the role of the [activeLocalSession]
     *
     * No-any params required
     */
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
        AnimatedVisibility(
            visible = !amITester.value
        ) {
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
    }

    /**
     * The [EquinoxAlertDialog] to warn the user about the deletion the current [release]
     *
     * No-any params required
     */
    @Composable
    private fun DeleteRelease() {
        EquinoxAlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
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

    /**
     * The custom [FloatingActionButton] to execute different actions on the [release] such upload
     * new assets or promote it
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun FAButton() {
        val isReleaseApproved = releaseStatus == Approved
        val authorizedToUpload = (activeLocalSession.isVendor && !amITester.value)
        AnimatedVisibility(
            visible = authorizedToUpload && releaseStatus != Latest && releaseStatus != Verifying
        ) {
            FloatingActionButton(
                containerColor = md_theme_light_primary,
                onClick = {
                    if (isReleaseApproved)
                        promoteRelease.value = true
                    else
                        launcher.launch()
                }
            ) {
                Icon(
                    imageVector = if (isReleaseApproved)
                        Icons.Default.Verified
                    else
                        Icons.Default.Upload,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            if (viewModel.requestedToUpload.value)
                UploadAssets()
            PromoteRelease()
        }
    }

    /**
     * Function to create and display the UI to promote the [release]
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun PromoteRelease() {
        if (releaseStatus == Approved) {
            val lastEventStatus = release.value!!.lastPromotionStatus
            val promotionInfo = getPromotionInfo(
                lastEventStatus = lastEventStatus
            )
            var newStatus = promotionInfo.second
            EquinoxAlertDialog(
                modifier = Modifier
                    .widthIn(
                        max = 400.dp
                    ),
                show = promoteRelease,
                icon = Icons.Default.Verified,
                viewModel = viewModel,
                title = getPromotionTitle(
                    lastEventStatus = lastEventStatus
                ),
                text = {
                    if (lastEventStatus == Approved) {
                        PromotionPathSelector(
                            onStatusChange = { status ->
                                newStatus = status
                            }
                        )
                    } else {
                        Text(
                            text = stringResource(promotionInfo.first),
                            textAlign = TextAlign.Justify
                        )
                    }
                },
                confirmAction = {
                    viewModel.promoteRelease(
                        projectId = projectId,
                        releaseId = releaseId,
                        newStatus = newStatus,
                        onResponse = { promoteRelease.value = false }
                    )
                },
                confirmText = Res.string.confirm,
                dismissText = Res.string.dismiss
            )
        }
    }

    /**
     * The dialog where the user can comment the uploaded assets and confirm or remove some assets
     * before to confirm their upload
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun UploadAssets() {
        AssetsDialog(
            onDismissRequest = {
                if (!viewModel.waitingAssetsManagement.value)
                    viewModel.resetUploadingInstances()
            }
        ) {
            UploadingSummary()
            WaitingManagementResult(
                info = Res.string.uploading_assets
            )
            UploadingResult()
        }
    }

    /**
     * The inner section of the [UploadingSummary] where are displayed the assets chosen to be uploaded
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun UploadingSummary() {
        AnimatedVisibility(
            visible = !viewModel.waitingAssetsManagement.value && viewModel.uploadingStatus.value == null
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    text = stringResource(Res.string.assets_summary),
                    fontSize = 22.sp,
                    fontStyle = Typography.titleLarge.fontStyle
                )
                EquinoxTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = viewModel.uploadingAssetsComment,
                    label = Res.string.comment,
                    isError = viewModel.uploadingAssetsCommentError,
                    validator = { isTagCommentValid(it) },
                    maxLines = 3
                )
                AssetsToUpload(
                    modifier = Modifier
                        .weight(2f),
                    uploadingAssets = viewModel.assetsToUpload
                )
                DialogsActions(
                    modifier = Modifier
                        .align(Alignment.End),
                    dismissAction = { viewModel.resetUploadingInstances() },
                    confirmAction = {
                        viewModel.uploadAssets(
                            projectId = projectId,
                            releaseId = releaseId
                        )
                    }
                )
            }
        }
    }

    /**
     * The inner section of the [UploadingSummary] where is displayed the result of the uploading of
     * the assets
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun UploadingResult() {
        AnimatedVisibility(
            visible = viewModel.uploadingStatus.value != null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val hasBeenSuccessful = viewModel.uploadingStatus.value == SUCCESSFUL
                val theme: ColorScheme = if (hasBeenSuccessful)
                    TesterThemeColor
                else
                    RedSchemeColors
                MaterialTheme(
                    colorScheme = theme
                ) {
                    if (hasBeenSuccessful) {
                        ErrorUI(
                            errorIcon = Icons.Outlined.Done,
                            errorColor = MaterialTheme.colorScheme.primary,
                            errorMessage = Res.string.uploading_assets_successful,
                            retryText = Res.string.close,
                            retryAction = { viewModel.resetUploadingInstances() }
                        )
                    } else {
                        ErrorUI(
                            errorIcon = Icons.Filled.Cancel,
                            errorColor = MaterialTheme.colorScheme.primary,
                            errorMessage = Res.string.uploading_assets_failed,
                            retryText = Res.string.retry,
                            retryAction = { viewModel.uploadingStatus.value = null }
                        )
                    }
                }
            }
        }
    }

    /**
     * Function to get the title for the [PromoteRelease] section
     *
     * @param lastEventStatus: the last event occurred in the current [release]
     *
     * @return the title as [StringResource]
     */
    @Composable
    @NonRestartableComposable
    private fun getPromotionTitle(
        lastEventStatus: ReleaseStatus
    ): StringResource {
        return when (lastEventStatus) {
            Approved -> Res.string.promote_release
            Alpha -> Res.string.promote_release_as_beta
            else -> Res.string.promote_release_as_latest
        }
    }

    /**
     * Function to get the information for the [PromoteRelease] section
     *
     * @param lastEventStatus: the last event occurred in the current [release]
     *
     * @return the warn text and the status chosen as [Pair] of [StringResource] and [ReleaseStatus]
     */
    private fun getPromotionInfo(
        lastEventStatus: ReleaseStatus
    ): Pair<StringResource, ReleaseStatus> {
        val promotionInfo: Pair<StringResource, ReleaseStatus>
        when (lastEventStatus) {
            Approved -> {
                promotionInfo = Pair(
                    first = Res.string.promote_release,
                    second = Alpha
                )
            }

            Alpha -> {
                promotionInfo = Pair(
                    first = Res.string.promote_beta_release_alert_message,
                    second = Beta
                )
            }

            else -> {
                promotionInfo = Pair(
                    first = Res.string.promote_latest_release_alert_message,
                    second = Latest
                )
            }
        }
        return promotionInfo
    }

    /**
     * Selector to chose the promotion path to follow for the current [release], so promote as the
     * [ReleaseStatus.Latest] directly or follow the release statuses from [Alpha] and then [Beta]
     *
     * @param onStatusChange: the action to execute when the status changes
     */
    @Composable
    @NonRestartableComposable
    private fun PromotionPathSelector(
        onStatusChange: (ReleaseStatus) -> Unit
    ) {
        var isAlphaSelected by remember { mutableStateOf(true) }
        var warnText by remember { mutableStateOf(Res.string.promote_alpha_release_alert_message) }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = isAlphaSelected,
                    onClick = {
                        if (!isAlphaSelected) {
                            isAlphaSelected = true
                            onStatusChange.invoke(Alpha)
                            warnText = Res.string.promote_alpha_release_alert_message
                        }
                    }
                )
                Text(
                    text = Alpha.name
                )
                RadioButton(
                    selected = !isAlphaSelected,
                    onClick = {
                        if (isAlphaSelected) {
                            isAlphaSelected = false
                            onStatusChange.invoke(Latest)
                            warnText = Res.string.promote_latest_release_alert_message
                        }
                    }
                )
                Text(
                    text = Latest.name
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = stringResource(warnText),
                textAlign = TextAlign.Justify
            )
        }
    }

    /**
     * Section where are displayed the events occurred in the current [release]
     *
     * @param paddingValues: the padding values to apply to the section
     */
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

    /**
     * The dedicated section to display the details about a [ReleaseEvent]
     *
     * @param event: the event to display
     * @param position: the position of the event in the container list
     */
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
                        EventTitle(
                            event = event
                        )
                        if (isAssetUploadingEvent) {
                            UploadingEventInfo(
                                event = event as AssetUploadingEvent
                            )
                        }
                    } else {
                        RejectedReleaseEventInfo(
                            event = event
                        )
                    }
                }
            }
        }
    }

    /**
     * The section where is displayed the title of the event
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun EventTitle(
        event: ReleaseEvent
    ) {
        val message = if (event is AssetUploadingEvent)
            Res.string.new_asset_has_been_uploaded
        else
            (event as ReleaseStandardEvent).getMessage()
        Text(
            text = stringResource(message),
        )
    }

    /**
     * The dedicated section to display the details about an [AssetUploadingEvent]
     *
     * @param event: the event to display
     */
    @Composable
    @NonRestartableComposable
    private fun UploadingEventInfo(
        event: AssetUploadingEvent
    ) {
        val comment = rememberRichTextState()
        comment.setMarkdown(event.comment)
        Column {
            Text(
                text = stringResource(Res.string.comment),
                fontSize = 14.sp
            )
            RichText(
                state = comment,
                fontFamily = thinFontFamily
            )
        }
        AnimatedVisibility(
            visible = releaseStatus != Approved && releaseStatus != Latest && !event.isCommented
        ) {
            viewModel.commentAsset = remember { mutableStateOf(false) }
            val chooseAssetsToDownload = remember { mutableStateOf(false) }
            CommentAssetsUploaded(
                event = event
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                val assetsUploaded = event.assetsUploaded
                val singleAssetAvailable = assetsUploaded.size == 1
                Button(
                    shape = RoundedCornerShape(
                        size = 10.dp
                    ),
                    onClick = {
                        if (singleAssetAvailable) {
                            viewModel.downloadTestAssets(
                                assetsUploaded = assetsUploaded
                            )
                        } else {
                            viewModel.suspendRefresher()
                            chooseAssetsToDownload.value = true
                        }
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.test)
                    )
                    if (viewModel.requestedToDownload.value && !singleAssetAvailable)
                        DownloadAssets()
                    if (chooseAssetsToDownload.value) {
                        ChoseAssetsToDownload(
                            show = chooseAssetsToDownload,
                            assetsUploaded = assetsUploaded
                        )
                    }
                }
                if (activeLocalSession.isCustomer || amITester.value) {
                    Button(
                        shape = RoundedCornerShape(
                            size = 10.dp
                        ),
                        onClick = { viewModel.commentAsset.value = true }
                    ) {
                        Text(
                            text = stringResource(Res.string.comment)
                        )
                    }
                }
            }
        }
    }

    /**
     * The custom dialog where the user can select the assets to download
     *
     * @param show: whether the dialog is visible or not
     * @param assetsUploaded: the list of the current assets available to be downloaded
     */
    @Composable
    @NonRestartableComposable
    private fun ChoseAssetsToDownload(
        show: MutableState<Boolean>,
        assetsUploaded: List<AssetUploaded>
    ) {
        AssetsDialog(
            onDismissRequest = {
                show.value = false
                viewModel.restartRefresher()
            }
        ) {
            AssetsSelector(
                show = show,
                assetsUploaded = assetsUploaded
            )
            WaitingManagementResult(
                info = Res.string.downloading_assets
            )
        }
    }

    /**
     * The inner section of the [ChoseAssetsToDownload] where the user can select the assets to download
     *
     * @param show: whether the dialog is visible or not
     * @param assetsUploaded: the list of the current assets available to be downloaded
     */
    @Composable
    @NonRestartableComposable
    private fun AssetsSelector(
        show: MutableState<Boolean>,
        assetsUploaded: List<AssetUploaded>
    ) {
        val selectionList = remember { mutableStateListOf<AssetUploaded>() }
        if (selectionList.isEmpty())
            selectionList.addAll(assetsUploaded)
        AnimatedVisibility(
            visible = !viewModel.waitingAssetsManagement.value
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 16.dp
                        ),
                    text = stringResource(Res.string.select_assets_to_download),
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontStyle = Typography.titleLarge.fontStyle
                )
                AssetsToDownload(
                    modifier = Modifier
                        .weight(2f),
                    selectionList = selectionList
                )
                DialogsActions(
                    modifier = Modifier
                        .align(Alignment.End),
                    dismissAction = {
                        show.value = false
                        viewModel.restartRefresher()
                    },
                    confirmAction = {
                        viewModel.downloadTestAssets(
                            assetsUploaded = selectionList,
                            onSuccess = { show.value = false }
                        )
                    }
                )
            }
        }
    }

    /**
     * The informative dialog to display the status about the assets downloading
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun DownloadAssets() {
        viewModel.suspendRefresher()
        AssetsDialog(
            onDismissRequest = {
                if (!viewModel.waitingAssetsManagement.value) {
                    viewModel.resetDownloadingInstances()
                    viewModel.restartRefresher()
                }
            }
        ) {
            WaitingManagementResult(
                info = Res.string.downloading_assets
            )
        }
    }

    /**
     * The informative section to display the result status about the assets uploading or downloading
     *
     * @param info: the info text to display
     */
    @Composable
    @NonRestartableComposable
    private fun WaitingManagementResult(
        info: StringResource
    ) {
        AnimatedVisibility(
            visible = viewModel.waitingAssetsManagement.value
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(150.dp),
                    strokeWidth = 10.dp
                )
                Text(
                    modifier = Modifier
                        .padding(
                            top = 32.dp
                        ),
                    text = stringResource(info),
                    fontFamily = thinFontFamily,
                    color = Color.Black
                )
            }
        }
    }

    /**
     * The [EquinoxAlertDialog] where the user can comment the assets uploaded
     *
     * @param event: the event where the assets are attached
     */
    @Composable
    @NonRestartableComposable
    private fun CommentAssetsUploaded(
        event: ReleaseEvent
    ) {
        viewModel.isApproved = remember { mutableStateOf(true) }
        viewModel.reasons = remember { mutableStateOf("") }
        viewModel.reasonsError = remember { mutableStateOf(false) }
        viewModel.rejectedTags = remember { mutableStateListOf() }
        EquinoxAlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
            show = viewModel.commentAsset,
            icon = Icons.AutoMirrored.Filled.Comment,
            viewModel = viewModel,
            onDismissAction = { viewModel.closeAction() },
            title = Res.string.comment_the_asset,
            text = { CommentRelease() },
            confirmAction = {
                viewModel.commentAssetsUploaded(
                    projectId = projectId,
                    releaseId = releaseId,
                    event = event
                )
            },
            confirmText = Res.string.confirm,
            dismissText = Res.string.dismiss
        )
    }

    /**
     * Function to create and display the UI to comment a release
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun CommentRelease() {
        Column(
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusSelector()
            AnimatedVisibility(
                visible = !viewModel.isApproved.value
            ) {
                RejectionSection()
            }
        }
    }

    /**
     * The inner session of the [CommentRelease] where the user can select if the release is [Approved]
     * or [Rejected]
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun StatusSelector() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusButton(
                isApproved = viewModel.isApproved.value,
                statusColor = Approved.createColor(),
                text = Res.string.approve,
                onClick = {
                    if (!viewModel.isApproved.value)
                        viewModel.isApproved.value = true
                }
            )
            StatusButton(
                isApproved = !viewModel.isApproved.value,
                statusColor = Rejected.createColor(),
                text = Res.string.reject,
                onClick = {
                    if (viewModel.isApproved.value)
                        viewModel.isApproved.value = false
                }
            )
        }
    }

    /**
     * The custom button to approve or reject the release
     *
     * @param isApproved: whether the release is approved
     * @param statusColor: the color of the button
     * @param text: the text of the button
     * @param onClick: the action to execute when the user click on the button
     */
    @Composable
    @NonRestartableComposable
    private fun StatusButton(
        isApproved: Boolean,
        statusColor: Color,
        text: StringResource,
        onClick: () -> Unit
    ) {
        OutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isApproved)
                    statusColor
                else
                    Color.Unspecified,
                contentColor = if (isApproved)
                    Color.White
                else
                    statusColor
            ),
            border = BorderStroke(
                width = 1.dp,
                color = statusColor
            ),
            modifier = Modifier
                .width(120.dp),
            onClick = onClick
        ) {
            Text(
                text = stringResource(text)
            )
        }
    }

    /**
     * The section dedicated to display the rejection details about the [release]
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun RejectionSection() {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EquinoxTextField(
                value = viewModel.reasons,
                label = Res.string.reasons,
                maxLines = 10,
                isError = viewModel.reasonsError,
                validator = { areRejectionReasonsValid(it) }
            )
            Text(
                text = stringResource(Res.string.tags),
                fontSize = 20.sp
            )
            LazyHorizontalGrid(
                modifier = Modifier
                    .requiredHeightIn(
                        min = 40.dp,
                        max = 80.dp
                    ),
                contentPadding = PaddingValues(
                    top = 5.dp
                ),
                rows = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(
                    key = { tag -> tag.name },
                    items = ReleaseTag.entries.toTypedArray()
                ) { tag ->
                    RejectedTagButton(
                        tag = tag
                    )
                }
            }
        }
    }

    /**
     * The custom button useful to display the reason about the selection of that specific [ReleaseTag]
     * or to fill that button
     *
     * @param tag: the tag to fill or display its reasons
     */
    @Composable
    @NonRestartableComposable
    private fun RejectedTagButton(
        tag: ReleaseTag
    ) {
        var isAdded by remember { mutableStateOf(false) }
        val tagColor = tag.createColor()
        OutlinedButton(
            modifier = Modifier
                .requiredWidthIn(
                    min = 40.dp,
                    max = 150.dp
                )
                .height(40.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isAdded)
                    tagColor
                else
                    Color.White
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (!isAdded)
                    tagColor
                else
                    Color.White
            ),
            onClick = {
                isAdded = !isAdded
                if (isAdded)
                    viewModel.rejectedTags.add(tag)
                else

                    viewModel.rejectedTags.remove(tag)
            }
        ) {
            Text(
                text = tag.name,
                fontWeight = FontWeight.Bold,
                color = if (!isAdded)
                    tagColor
                else
                    Color.White
            )
        }
    }

    /**
     * The section dedicated to display the details about a [RejectedReleaseEvent]
     *
     * @param event: the event to display
     */
    @Composable
    @NonRestartableComposable
    private fun RejectedReleaseEventInfo(
        event: RejectedReleaseEvent
    ) {
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
                        isTester = amITester.value,
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
            val isInputMode = tag.comment.isNullOrEmpty()
            viewModel.rejectedTagDescription = remember { mutableStateOf("") }
            viewModel.rejectedTagDescriptionError = remember { mutableStateOf(false) }
            val isInputButton = @Composable {
                TextButton(
                    onClick = {
                        viewModel.rejectedTagDescription.value = ""
                        viewModel.rejectedTagDescriptionError.value = false
                        viewModel.restartRefresher()
                        show.value = false
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.dismiss)
                    )
                }
            }
            val dismissButton = if (isInputMode)
                isInputButton
            else
                null
            NovaAlertDialog(
                show = show,
                tag = tag,
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = date,
                            fontFamily = thinFontFamily,
                        )
                        if (!isInputMode) {
                            Text(
                                text = tag.comment,
                                textAlign = TextAlign.Justify
                            )
                        } else {
                            EquinoxTextField(
                                value = viewModel.rejectedTagDescription,
                                label = Res.string.description,
                                isError = viewModel.rejectedTagDescriptionError,
                                validator = { isTagCommentValid(it) },
                                maxLines = 5
                            )
                        }
                    }
                },
                confirmText = if (isInputMode)
                    Res.string.confirm
                else
                    Res.string.close,
                dismissButton = dismissButton,
                confirmAction = {
                    if (isInputMode) {
                        viewModel.fillRejectedTag(
                            projectId = projectId,
                            releaseId = releaseId,
                            event = event,
                            tag = tag,
                            onSuccess = { show.value = false }
                        )
                    } else
                        show.value = false
                }
            )
        }
    }

    @TestOnly
    @Deprecated(
        message = "Will be replaced with the Equinox's one"
    )
    @Composable
    @NonRestartableComposable
    private fun NovaAlertDialog(
        show: MutableState<Boolean>,
        tag: RejectedTag,
        text: @Composable () -> Unit,
        confirmAction: () -> Unit,
        confirmText: StringResource,
        dismissButton: @Composable (() -> Unit)?
    ) {
        if (show.value) {
            viewModel.suspendRefresher()
            AlertDialog(
                modifier = Modifier
                    .widthIn(
                        max = 400.dp
                    ),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null
                    )
                },
                onDismissRequest = {
                    viewModel.rejectedTagDescription.value = ""
                    viewModel.rejectedTagDescriptionError.value = false
                    viewModel.restartRefresher()
                    show.value = false
                },
                title = {
                    Text(
                        text = tag.tag.name
                    )
                },
                text = text,
                dismissButton = dismissButton,
                confirmButton = {
                    TextButton(
                        onClick = {
                            confirmAction.invoke()
                            viewModel.restartRefresher()
                        }
                    ) {
                        Text(
                            text = stringResource(confirmText)
                        )
                    }
                }
            )
        }
    }

    /**
     * Function invoked when the [ShowContent] composable has been created
     *
     * No-any params required
     */
    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(this::class.java)
    }

    /**
     * Function invoked when the [ShowContent] composable has been started
     *
     * No-any params required
     */
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
        if (!deleteRelease.value && !promoteRelease.value && !viewModel.requestedToUpload.value)
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

    /**
     * Function to collect or instantiate the states of the screen
     *
     * No-any params required
     */
    @Composable
    override fun CollectStates() {
        super.CollectStates()
        amITester = remember { mutableStateOf(false) }
        release = viewModel.release.collectAsState()
        release.value?.let { release: Release ->
            releaseStatus = release.status
            releaseProject = release.project
            amITester.value = activeLocalSession.isTester(releaseProject)
        }
        deleteRelease = remember { mutableStateOf(false) }
        promoteRelease = remember { mutableStateOf(false) }
        launcher = rememberFilePickerLauncher(mode = PickerMode.Multiple()) { files ->
            files?.forEach { asset ->
                val assetPath = getAsset(
                    asset = asset
                )
                if (assetPath != null)
                    viewModel.assetsToUpload.add(File(assetPath))
            }
            if (viewModel.assetsToUpload.isNotEmpty()) {
                viewModel.suspendRefresher()
                viewModel.requestedToUpload.value = true
            }
        }
        viewModel.uploadingAssetsComment = remember { mutableStateOf("") }
        viewModel.uploadingAssetsCommentError = remember { mutableStateOf(false) }
        viewModel.requestedToUpload = remember { mutableStateOf(false) }
        viewModel.requestedToDownload = remember { mutableStateOf(false) }
        viewModel.assetsToUpload = remember { mutableStateListOf() }
        viewModel.waitingAssetsManagement = remember { mutableStateOf(false) }
        viewModel.uploadingStatus = remember { mutableStateOf(null) }
    }

}