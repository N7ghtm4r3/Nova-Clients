package com.tecknobit.nova.ui.screens.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.ADD_MEMBERS_DIALOG
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import com.tecknobit.novacore.records.release.Release
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Component to display and arrange correctly from each platform the releases in the UI
 *
 * @param paddingValues: the padding values to apply to the section
 * @param project: the project where the release are attached
 * @param onEdit: the action to execute when the user request to edit a release
 */
@NonRestartableComposable
@Composable
actual fun Releases(
    paddingValues: PaddingValues,
    project: Project,
    onEdit: (Release) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding()
            ),
        columns = GridCells.Adaptive(300.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            key = { release -> release.id },
            items = project.releases
        ) { release ->
            ReleaseItem(
                fillMaxHeight = true,
                project = project,
                release = release,
                onEdit = onEdit
            )
        }
    }
}

/**
 * Function to navigate to the section where the user can add member to a project
 *
 * @param project: the project where add the members
 */
actual fun addMembers(
    project: Project
) {
    MainScope().launch {
        val currentEntry = navigator.currentEntry.first()
        currentEntry?.stateHolder?.set(PROJECT_KEY, project)
    }
    navigator.navigate(ADD_MEMBERS_DIALOG)
}