@file:OptIn(ExperimentalMaterial3Api::class)

package com.tecknobit.nova.ui.screens.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinoxcompose.components.EmptyListUI
import com.tecknobit.equinoxcompose.components.EquinoxAlertDialog
import com.tecknobit.equinoxcompose.components.EquinoxOutlinedTextField
import com.tecknobit.equinoxcompose.helpers.session.ManagedContent
import com.tecknobit.nova.navigator
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.ui.components.MemberListItem
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.novacore.NovaInputValidator.areReleaseNotesValid
import com.tecknobit.novacore.NovaInputValidator.isReleaseVersionValid
import com.tecknobit.novacore.records.NovaUser
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res.string
import nova.composeapp.generated.resources.add_release
import nova.composeapp.generated.resources.confirm
import nova.composeapp.generated.resources.delete_project
import nova.composeapp.generated.resources.delete_project_alert_message
import nova.composeapp.generated.resources.dismiss
import nova.composeapp.generated.resources.leave_from_project
import nova.composeapp.generated.resources.leave_project_alert_message
import nova.composeapp.generated.resources.no_releases_yet
import nova.composeapp.generated.resources.release_notes
import nova.composeapp.generated.resources.release_version
import nova.composeapp.generated.resources.wrong_release_notes
import nova.composeapp.generated.resources.wrong_release_version

class ProjectScreen(
    private val projectId: String
) : NovaScreen() {

    companion object {

        private val viewModel = ProjectScreenViewModel(
            snackbarHostState = snackbarHostState
        )

    }

    private lateinit var project: State<Project?>
    
    private var amITheProjectAuthor: Boolean = false

    /**
     * **showMembers** -> state used to display the [ProjectMembers] UI
     */
    private lateinit var showMembers: MutableState<Boolean>

    /**
     * **workOnProject** -> state used to display the [EquinoxAlertDialog] shown to delete or leave from a project
     */
    private lateinit var workOnProject: MutableState<Boolean>

    /**
     * **addRelease** -> state used to display the [EquinoxAlertDialog] to add a new release
     */
    private lateinit var addRelease: MutableState<Boolean>
    
    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        CollectStates()
        AnimatedVisibility(
            visible = project.value != null,
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
                                title = { ProjectTitle() },
                                actions = { Actions() }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        floatingActionButton = {
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.primary,
                                onClick = { addRelease.value = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            AddRelease()
                        }
                    ) {
                        ReleasesSection(
                            paddingValues = it
                        )
                    }
                }
            )
        }
        LoadingData(
            item = project
        )
    }

    @Composable
    @NonRestartableComposable
    private fun ProjectTitle() {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                modifier = Modifier
                    .alignBy(LastBaseline),
                text = project.value!!.name,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            val workingProgressVersion = project.value!!.workingProgressVersion
            if(workingProgressVersion != null) {
                Text(
                    modifier = Modifier
                        .alignBy(LastBaseline),
                    text = workingProgressVersion,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }

    @Composable
    @NonRestartableComposable
    private fun Actions() {
        if(activeLocalSession.isVendor) {
            IconButton(
                onClick = {
                    /*suspendRefresher()
                    showAddMembers.value = true*/
                }
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            //AddMembers()
        }
        IconButton(
            onClick = { showMembers.value = true }
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = Color.White
            )
        }
        ProjectMembers()
        IconButton(
            onClick = { workOnProject.value = true }
        ) {
            Icon(
                imageVector = if(amITheProjectAuthor)
                    Icons.Default.DeleteForever
                else
                    Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = Color.White
            )
        }
        WarnAlertDialog()
    }

    @Composable
    @NonRestartableComposable
    private fun ProjectMembers() {
        if(showMembers.value) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(),
                containerColor = gray_background,
                onDismissRequest = { showMembers.value = false }
            ) {
                LazyColumn {
                    items(
                        key = { member -> member.id },
                        items = project.value!!.projectMembers
                    ) { member ->
                        Member(
                            member = member
                        )
                    }
                }
            }
        }
    }

    @Composable
    @NonRestartableComposable
    private fun Member(
        member: NovaUser
    ) {
        MemberListItem(
            member = member,
            trailingContent = {
                if(amITheProjectAuthor && activeLocalSession.id != member.id) {
                    IconButton(
                        onClick = {
                            viewModel.removeMember(
                                member = member
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonRemove,
                            null
                        )
                    }
                }
            }
        )
    }

    @Composable
    @NonRestartableComposable
    private fun WarnAlertDialog() {
        EquinoxAlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
            show = workOnProject,
            viewModel = viewModel,
            icon = if(amITheProjectAuthor)
                Icons.Default.DeleteForever
            else
                Icons.AutoMirrored.Filled.ExitToApp,
            title = if(amITheProjectAuthor)
                string.delete_project
            else
                string.leave_from_project,
            text = if(amITheProjectAuthor)
                string.delete_project_alert_message
            else
                string.leave_project_alert_message,
            confirmAction = {
                viewModel.workOnProject(
                    amITheProjectAuthor = amITheProjectAuthor,
                    onSuccess = {
                        workOnProject.value = false
                        navigator.goBack()
                    }
                )
            },
            confirmText = string.confirm,
            dismissText = string.dismiss
        )
    }

    @Composable
    @NonRestartableComposable
    private fun ReleasesSection(
        paddingValues: PaddingValues
    ) {
        val releases = project.value!!.releases
        if (releases.isNotEmpty()) {
            Releases(
                paddingValues = paddingValues,
                project = project.value!!
            )
        } else {
            EmptyListUI(
                icon = Icons.AutoMirrored.Filled.LibraryBooks,
                subText = string.no_releases_yet
            )
        }
    }


    @Composable
    private fun AddRelease() {
        viewModel.releaseVersion = remember { mutableStateOf("") }
        viewModel.releaseVersionError = remember { mutableStateOf(false) }
        viewModel.releaseNotes = remember { mutableStateOf("") }
        viewModel.releaseNotesError = remember { mutableStateOf(false) }
        EquinoxAlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
            show = addRelease,
            icon = Icons.Default.Add,
            viewModel = viewModel,
            title = string.add_release,
            text = {
                Column {
                    EquinoxOutlinedTextField(
                        label = string.release_version,
                        value = viewModel.releaseVersion,
                        validator = { isReleaseVersionValid(it) },
                        isError = viewModel.releaseVersionError,
                        errorText = string.wrong_release_version
                    )
                    EquinoxOutlinedTextField(
                        label = string.release_notes,
                        value = viewModel.releaseNotes,
                        validator = { areReleaseNotesValid(it) },
                        isError = viewModel.releaseNotesError,
                        errorText = string.wrong_release_notes,
                        maxLines = 10
                    )
                }
            },
            confirmAction = {
                viewModel.addRelease { addRelease.value = false }
            },
            confirmText = string.confirm,
            dismissText = string.dismiss
        )
    }

    @Composable
    override fun CollectStates() {
        super.CollectStates()
        project = viewModel.project.collectAsState()
        project.value?.let { project: Project ->
            amITheProjectAuthor = project.amITheProjectAuthor(activeLocalSession.id)
        }
        showMembers = remember { mutableStateOf(false) }
        workOnProject = remember { mutableStateOf(false) }
        addRelease = remember { mutableStateOf(false) }
    }

    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(this::class.java)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getProject(
            projectId = projectId
        )
    }

    /**
     * Function invoked when the [ShowContent] composable has been resumed
     *
     * No-any params required
     */
    override fun onResume() {
        super.onResume()
        if (!workOnProject.value && !addRelease.value)
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