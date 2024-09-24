package com.tecknobit.nova.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.equinoxcompose.components.EmptyListUI
import com.tecknobit.equinoxcompose.helpers.session.ManagedContent
import com.tecknobit.nova.Logo
import com.tecknobit.nova.getProjectLogoUrl
import com.tecknobit.nova.helpers.utils.waitToStart
import com.tecknobit.nova.imageLoader
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.profile.navToProfile
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.logo
import nova.composeapp.generated.resources.no_projects_yet
import nova.composeapp.generated.resources.no_version_available_yet
import nova.composeapp.generated.resources.projects
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ProjectsScreen : NovaScreen() {

    private val viewModel = ProjectsScreenViewModel(
        snackbarHostState = snackbarHostState
    )

    private lateinit var projects: State<List<Project>>

    init {
        waitToStart(
            action = {
                viewModel.setActiveContext(this::class.java)
                viewModel.getProjects()
            }
        )
    }

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        CollectStates()
        ManagedContent(
            viewModel = viewModel,
            content = {
                Scaffold (
                    floatingActionButton = {
                        Column (
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if(activeLocalSession.isVendor) {
                                FloatingActionButton(
                                    onClick = {
                                        // TODO: TO SET
                                        /*displayAddProject.value = true
                                        suspendRefresher()*/
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                                // AddProject()
                            }
                            FloatingActionButton(
                                onClick = {
                                    // TODO: TO SET
                                    /*   suspendRefresher()
                                       barcodeLauncher.launch(scanOptions)*/
                                },
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) {
                    Column (
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxSize()
                    ) {
                        UserDetails()
                        Projects()
                    }
                }
            }
        )
    }

    @Composable
    override fun CollectStates() {
        super.CollectStates()
        projects = viewModel.projects.collectAsState()
    }

    @Composable
    @NonRestartableComposable
    private fun UserDetails() {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .shadow(
                        elevation = 5.dp,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable { navToProfile() }
                    .size(150.dp),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(activeLocalSession.profilePicUrl)
                    .crossfade(500)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = painterResource(Res.drawable.logo)
            )
            Text(
                modifier = Modifier
                    .padding(
                        top = 5.dp
                    ),
                text = activeLocalSession.role.name.uppercase(),
                fontSize = 25.sp,
                color = Color.White
            )
        }
    }

    @Composable
    @NonRestartableComposable
    private fun Projects() {
        Card (
            shape = RoundedCornerShape(
                topStart = 35.dp,
                topEnd = 35.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = gray_background
            ),
            elevation = CardDefaults.cardElevation(10.dp),
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 15.dp,
                        end = 15.dp,
                        bottom = 15.dp,
                        start = 20.dp
                    )
            ) {
                if(projects.value.isEmpty()) {
                    EmptyListUI(
                        icon = Icons.Default.FolderOff,
                        subText = Res.string.no_projects_yet
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.projects),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 10.dp,
                            bottom = 10.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            key = { project -> project.id },
                            items = projects.value
                        ) { project ->
                            ProjectItem(
                                project = project
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    @NonRestartableComposable
    private fun ProjectItem(
        project: Project
    ) {
        ListItem(
            modifier = Modifier
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(15.dp)
                )
                .clip(RoundedCornerShape(15.dp))
                .clickable {

                    // TODO: TO SET
                    /*val intent = Intent(
                        this@MainActivity,
                        ProjectActivity::class.java
                    )
                    intent.putExtra(
                        PROJECT_IDENTIFIER_KEY,
                        project.id
                    )
                    startActivity(intent)*/
                },
            colors = ListItemDefaults.colors(
                containerColor = Color.White
            ),
            leadingContent = {
                BadgedBox(
                    badge = {
                        // TODO: TO SET
                        /*val notifications = project.getNotifications(
                            notifications
                        )
                        if(notifications > 0) {
                            Badge {
                                Text(
                                    text = "$notifications"
                                )
                            }
                        }*/
                    }
                ) {
                    Logo(
                        url = getProjectLogoUrl(project)
                    )
                }
            },
            headlineContent = {
                Text(
                    text = project.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            supportingContent = {
                val workingProgressVersionText = project.workingProgressVersion
                Text(
                    text = if(workingProgressVersionText != null)
                        workingProgressVersionText
                    else
                        stringResource(Res.string.no_version_available_yet),
                    fontSize = 16.sp
                )
            },
            trailingContent = {
                Icon(
                    modifier = Modifier
                        .size(30.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
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