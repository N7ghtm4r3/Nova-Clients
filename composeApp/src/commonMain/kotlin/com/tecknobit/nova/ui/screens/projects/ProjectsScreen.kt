package com.tecknobit.nova.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.tecknobit.nova.imageLoader
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.profile.navToProfile
import com.tecknobit.nova.ui.theme.gray_background
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.logo
import nova.composeapp.generated.resources.no_projects_yet
import nova.composeapp.generated.resources.projects
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The [ProjectsScreen] class is used to retrieve and display the projects of the user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxScreen
 * @see NovaScreen
 */
class ProjectsScreen : NovaScreen() {

    companion object {

        /**
         * *viewModel* -> the support view model to manage the requests to the backend
         */
        private val viewModel = ProjectsScreenViewModel(
            snackbarHostState = snackbarHostState
        )

    }

    /**
     * **projects** -> the projects list of the user
     */
    private lateinit var projects: State<List<Project>>

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        CollectStates()
        CloseApplicationOnNavBack()
        ManagedContent(
            viewModel = viewModel,
            content = {
                Scaffold (
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        Column (
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if(activeLocalSession.isVendor) {
                                FloatingActionButton(
                                    onClick = { workOnProject() },
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }
                            FloatingActionButton(
                                onClick = { joinProject() },
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                ) {
                    Column (
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxSize()
                    ) {
                        UserDetails()
                        ProjectsSection()
                    }
                }
            }
        )
    }

    /**
     * The user profile picture where clicking on it can be possible navigate to the [ProfileScreen]
     *
     * No-any params required
     */
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

    /**
     * The section where are displayed the projects and their details
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun ProjectsSection() {
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
                    Projects(
                        projects = projects.value
                    )
                }
            }
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
        viewModel.getProjects()
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

    /**
     * Function to collect or instantiate the states of the screen
     *
     * No-any params required
     */
    @Composable
    override fun CollectStates() {
        super.CollectStates()
        projects = viewModel.projects.collectAsState()
    }

}