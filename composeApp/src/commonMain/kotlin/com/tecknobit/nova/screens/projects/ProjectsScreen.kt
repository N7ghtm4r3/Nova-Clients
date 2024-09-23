package com.tecknobit.nova.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.nova.imageLoader
import com.tecknobit.nova.screens.NovaScreen
import com.tecknobit.nova.screens.SplashScreen.Companion.activeLocalSession
import com.tecknobit.nova.theme.gray_background
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

class ProjectsScreen : NovaScreen() {

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
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
                            .clickable {
                                // TODO: TO SET
                                /*startActivity(
                                    Intent(this@MainActivity, ProfileActivity::class.java)
                                )*/
                            }
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
                        /*if(projects.isNotEmpty()) {
                            Text(
                                text = getString(R.string.projects),
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
                                    items = projects
                                ) { project ->
                                    ListItem(
                                        modifier = Modifier
                                            .shadow(
                                                elevation = 5.dp,
                                                shape = RoundedCornerShape(15.dp)
                                            )
                                            .clip(RoundedCornerShape(15.dp))
                                            .clickable {
                                                val intent = Intent(
                                                    this@MainActivity,
                                                    ProjectActivity::class.java
                                                )
                                                intent.putExtra(
                                                    PROJECT_IDENTIFIER_KEY,
                                                    project.id
                                                )
                                                startActivity(intent)
                                            },
                                        colors = ListItemDefaults.colors(
                                            containerColor = Color.White
                                        ),
                                        leadingContent = {
                                            BadgedBox(
                                                badge = {
                                                    val notifications = project.getNotifications(
                                                        notifications
                                                    )
                                                    if(notifications > 0) {
                                                        Badge {
                                                            Text(
                                                                text = "$notifications"
                                                            )
                                                        }
                                                    }
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
                                                    getString(R.string.no_version_available_yet),
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
                            }
                        } else {
                            EmptyList(
                                icon = Icons.Default.FolderOff,
                                description = R.string.no_projects_yet
                            )
                        }*/
                    }
                }
            }
        }
    }

}