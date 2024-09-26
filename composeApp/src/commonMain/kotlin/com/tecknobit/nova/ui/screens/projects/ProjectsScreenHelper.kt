@file:OptIn(ExperimentalFoundationApi::class)

package com.tecknobit.nova.ui.screens.projects

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.nova.Logo
import com.tecknobit.nova.getProjectLogoUrl
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECT_SCREEN
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.no_version_available_yet
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun Projects(
    projects: List<Project>
)

expect fun workOnProject(
    project: Project? = null
)

@Composable
@NonRestartableComposable
fun ProjectItem(
    project: Project
) {
    ListItem(
        modifier = Modifier
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
            .combinedClickable(
                onClick = { navigator.navigate("$PROJECT_SCREEN/${project.id}") },
                onLongClick = {
                    // TODO: TO SET
                }
            ),
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