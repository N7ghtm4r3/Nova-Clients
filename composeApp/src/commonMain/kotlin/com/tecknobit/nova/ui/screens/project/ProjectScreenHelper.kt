@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalRichTextApi::class,
    ExperimentalMaterial3Api::class
)

package com.tecknobit.nova.ui.screens.project

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.tecknobit.nova.navigator
import com.tecknobit.nova.thinFontFamily
import com.tecknobit.nova.ui.components.ReleaseStatusBadge
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.RELEASE_SCREEN
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.notifications
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.release.Release
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import nova.composeapp.generated.resources.Res.string
import nova.composeapp.generated.resources.approbation_date
import nova.composeapp.generated.resources.creation_date
import nova.composeapp.generated.resources.release_notes
import org.jetbrains.compose.resources.stringResource

/**
 * Component to display and arrange correctly from each platform the releases in the UI
 *
 * @param paddingValues: the padding values to apply to the section
 * @param project: the project where the release are attached
 * @param onEdit: the action to execute when the user request to edit a release
 */
@Composable
@NonRestartableComposable
expect fun Releases(
    paddingValues: PaddingValues,
    project: Project,
    onEdit: (Release) -> Unit
)

/**
 * Component to display the details about a release
 *
 * @param fillMaxHeight: whether the container [OutlinedCard] must fill the max height
 * @param project: the project where the release are attached
 * @param release: the release to display
 * @param onEdit: the action to execute when the user request to edit a release
 */
@Composable
@NonRestartableComposable
fun ReleaseItem(
    fillMaxHeight: Boolean = false,
    project: Project,
    release: Release,
    onEdit: (Release) -> Unit
) {
    val releaseNotes = rememberRichTextState()
    releaseNotes.setMarkdown(release.releaseNotes)
    val expandReleaseNotes = remember { mutableStateOf(false) }
    OutlinedCard(
        modifier = Modifier
            .heightIn(
                max = 250.dp
            )
            .combinedClickable(
                onClick = {
                    navigator.navigate("$RELEASE_SCREEN/${project.id}/${release.id}")
                },
                onDoubleClick = { expandReleaseNotes.value = true },
                onLongClick = if (!activeLocalSession.isTester(project)) {
                    {
                        onEdit.invoke(release)
                    }
                } else
                    null
            ),
        colors = CardDefaults
            .outlinedCardColors(
                containerColor = Color.White
            ),
        elevation = CardDefaults
            .cardElevation(
                defaultElevation = 5.dp
            ),
        shape = RoundedCornerShape(10.dp)
    ) {
        BadgedBox(
            badge = {
                val notifications = release.getNotifications(notifications)
                androidx.compose.animation.AnimatedVisibility(
                    visible = notifications > 0
                ) {
                    Badge (
                        modifier = Modifier
                            .padding(
                                top = 10.dp
                            )
                            .size(
                                width = 40.dp,
                                height = 25.dp
                            )
                    ) {
                        Text(
                            text = "$notifications"
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        all = 16.dp
                    ).then(
                        if (fillMaxHeight)
                            Modifier.fillMaxHeight()
                        else
                            Modifier
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = release.releaseVersion,
                        fontSize = 20.sp
                    )
                    ReleaseStatusBadge(
                        releaseStatus = release.status
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(
                            top = 5.dp,
                            start = 5.dp
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(string.creation_date),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = release.creationDate,
                        fontSize = 16.sp,
                        fontFamily = thinFontFamily
                    )
                }
                if (release.status == ReleaseStatus.Approved) {
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 5.dp
                            )
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = stringResource(string.approbation_date),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = release.approbationDate,
                            fontSize = 16.sp,
                            fontFamily = thinFontFamily
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(
                            top = 5.dp,
                        ),
                    text = stringResource(string.release_notes),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                RichText(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    state = releaseNotes,
                    fontFamily = thinFontFamily
                )
            }
        }
    }
    ExpandReleaseNotes(
        expand = expandReleaseNotes,
        releaseVersion = release.releaseVersion,
        releaseNotes = releaseNotes
    )
}

/**
 * Section to read the notes of a release in a full-size format
 *
 * @param expand: whether hide or hidden the section
 * @param releaseVersion: the version of the release
 * @param releaseNotes: the notes of the release
 */
@Composable
@NonRestartableComposable
private fun ExpandReleaseNotes(
    expand: MutableState<Boolean>,
    releaseVersion: String,
    releaseNotes: RichTextState
) {
    if (expand.value) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = { expand.value = false }
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        all = 16.dp
                    )
            ) {
                Text(
                    text = releaseVersion,
                    fontSize = 20.sp
                )
                HorizontalDivider()
                RichText(
                    modifier = Modifier
                        .padding(
                            top = 10.dp
                        )
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize(),
                    state = releaseNotes,
                    fontFamily = thinFontFamily
                )
            }
        }
    }
}

/**
 * Function to navigate to the section where the user can add member to a project
 *
 * @param project: the project where add the members
 */
expect fun addMembers(
    project: Project
)