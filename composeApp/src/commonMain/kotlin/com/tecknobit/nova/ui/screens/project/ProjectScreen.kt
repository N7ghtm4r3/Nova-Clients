@file:OptIn(ExperimentalMaterial3Api::class)

package com.tecknobit.nova.ui.screens.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinoxcompose.components.EmptyListUI
import com.tecknobit.equinoxcompose.helpers.session.ManagedContent
import com.tecknobit.nova.navigator
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.loading_data

class ProjectScreen(
    val projectId: String
) : NovaScreen() {

    companion object {

        private val viewModel = ProjectScreenViewModel(
            snackbarHostState = SnackbarHostState()
        )

    }

    private lateinit var project: State<Project?>
    
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
                                navigationIcon = {
                                    IconButton(
                                        onClick = { navigator.goBack() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                },
                                title = {
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
                                },
                                actions = {
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
                                        onClick = { /*showMembers.value = true*/ }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Group,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    //ShowMembers()
                                    IconButton(
                                        onClick = {
                                            /*workOnProject.value = true
                                            suspendRefresher()*/
                                        }
                                    ) {
                                        Icon(
                                            imageVector = /*if(isProjectAuthor)
                                    Icons.Default.DeleteForever
                                else*/
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    /*val goBack = {
                                        workOnProject.value = false
                                        navigator.goBack()
                                    }
                                    val failureAction = {
                                        workOnProject.value = false
                                        refreshItem()
                                    }
                                    NovaAlertDialog(
                                        show = workOnProject,
                                        onDismissAction = failureAction,
                                        icon = if(isProjectAuthor)
                                            Icons.Default.DeleteForever
                                        else
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                        title = if(isProjectAuthor)
                                            string.delete_project
                                        else
                                            string.leave_from_project,
                                        message = if(isProjectAuthor)
                                            string.delete_project_alert_message
                                        else
                                            string.leave_project_alert_message,
                                        confirmAction = {
                                            if(isProjectAuthor) {
                                                requester.sendRequest(
                                                    request = {
                                                        requester.deleteProject(
                                                            projectId = project.value.id
                                                        )
                                                    },
                                                    onSuccess = {
                                                        goBack()
                                                    },
                                                    onFailure = { response ->
                                                        failureAction()
                                                        snackbarLauncher.showSnack(
                                                            message = response.getString(RESPONSE_MESSAGE_KEY)
                                                        )
                                                    }
                                                )
                                            } else {
                                                requester.sendRequest(
                                                    request = {
                                                        requester.leaveProject(
                                                            projectId = project.value.id
                                                        )
                                                    },
                                                    onSuccess = {
                                                        goBack()
                                                    },
                                                    onFailure = { response ->
                                                        failureAction()
                                                        snackbarLauncher.showSnack(
                                                            message = response.getString(RESPONSE_MESSAGE_KEY)
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    )*/
                                }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        floatingActionButton = {
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.primary,
                                onClick = {
                                    /*suspendRefresher()
                                    showAddRelease.value = true*/
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            /*val releaseVersion = remember { mutableStateOf("") }
                            val releaseVersionError = remember { mutableStateOf(false) }
                            val releaseVersionErrorMessage = remember { mutableStateOf("") }
                            val releaseNotes = remember { mutableStateOf("") }
                            val releaseNotesError = remember { mutableStateOf(false) }
                            val releaseNotesErrorMessage = remember { mutableStateOf("") }
                            val state = rememberRichTextState()
                            val resetLayout = {
                                releaseVersion.value = ""
                                releaseVersionError.value = false
                                releaseNotes.value = ""
                                state.setMarkdown("")
                                releaseNotesError.value = false
                                releaseNotesErrorMessage.value = ""
                                showAddRelease.value = false
                                refreshItem()
                            }
                            NovaAlertDialog(
                                modifier = Modifier
                                    .width(750.dp)
                                    .heightIn(
                                        max = 500.dp
                                    ),
                                show = showAddRelease,
                                onDismissAction = { resetLayout() },
                                icon = Icons.Default.NewReleases,
                                title = string.add_release,
                                message = {
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Column (
                                            modifier = Modifier
                                                .weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            NovaTextField(
                                                value = releaseVersion,
                                                onValueChange = {
                                                    releaseVersionError.value = !isReleaseVersionValid(it) &&
                                                            releaseVersion.value.isNotEmpty()
                                                    checkToSetErrorMessage(
                                                        errorMessage = releaseVersionErrorMessage,
                                                        errorMessageKey = string.wrong_release_version,
                                                        error = releaseVersionError
                                                    )
                                                    releaseVersion.value = it
                                                },
                                                label = string.release_version,
                                                errorMessage = releaseVersionErrorMessage,
                                                isError = releaseVersionError
                                            )
                                            NovaTextField(
                                                modifier = Modifier
                                                    .fillMaxHeight(),
                                                singleLine = false,
                                                value = releaseNotes,
                                                onValueChange = {
                                                    releaseNotesError.value = !areReleaseNotesValid(it) &&
                                                            releaseNotes.value.isNotEmpty()
                                                    checkToSetErrorMessage(
                                                        errorMessage = releaseNotesErrorMessage,
                                                        errorMessageKey = string.wrong_release_notes,
                                                        error = releaseNotesError
                                                    )
                                                    releaseNotes.value = it
                                                    state.setMarkdown(it)
                                                },
                                                trailingIcon = {
                                                    IconButton(
                                                        onClick = {
                                                            releaseNotes.value = ""
                                                            state.setMarkdown("")
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Clear,
                                                            contentDescription = null
                                                        )
                                                    }
                                                },
                                                label = string.release_version,
                                                errorMessage = releaseNotesErrorMessage,
                                                isError = releaseNotesError
                                            )
                                        }
                                        VerticalDivider()
                                        Column (
                                            modifier = Modifier
                                                .weight(1f)
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .padding(
                                                        top = 7.dp
                                                    ),
                                                text = stringResource(string.preview),
                                                fontSize = 22.sp
                                            )
                                            RichText(
                                                modifier = Modifier
                                                    .verticalScroll(rememberScrollState()),
                                                state = state
                                            )
                                        }

                                    }
                                },
                                dismissAction = { resetLayout() },
                                confirmAction = {
                                    if(isReleaseVersionValid(releaseVersion.value)) {
                                        if(areReleaseNotesValid(releaseNotes.value)) {
                                            requester.sendRequest(
                                                request = {
                                                    requester.addRelease(
                                                        projectId = project.value.id,
                                                        releaseVersion = releaseVersion.value,
                                                        releaseNotes = releaseNotes.value
                                                    )
                                                },
                                                onSuccess = {
                                                    resetLayout()
                                                },
                                                onFailure = { response ->
                                                    resetLayout()
                                                    snackbarLauncher.showSnack(
                                                        message = response.getString(RESPONSE_MESSAGE_KEY)
                                                    )
                                                }
                                            )
                                        } else {
                                            setErrorMessage(
                                                errorMessage = releaseNotesErrorMessage,
                                                errorMessageValue = string.wrong_release_notes,
                                                error = releaseNotesError
                                            )
                                        }
                                    } else {
                                        setErrorMessage(
                                            errorMessage = releaseVersionErrorMessage,
                                            errorMessageValue = string.wrong_release_version,
                                            error = releaseVersionError
                                        )
                                    }
                                }
                            )*/
                        }
                    ) {

                    }
                }
            )
        }
        AnimatedVisibility(
            visible = project.value == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptyListUI(
                icon = Icons.Default.Downloading,
                subText = Res.string.loading_data
            )
        }
    }

    @Composable
    override fun CollectStates() {
        super.CollectStates()
        project = viewModel.project.collectAsState()
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