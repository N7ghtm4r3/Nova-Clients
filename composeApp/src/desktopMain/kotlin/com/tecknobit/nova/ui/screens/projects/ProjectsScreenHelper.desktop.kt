package com.tecknobit.nova.ui.screens.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.JOIN_PROJECT_DIALOG
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.WORK_ON_PROJECT_DIALOG
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Component to display and arrange correctly from each platform the projects in the UI
 *
 * @param projects: the projects list to display
 */
@Composable
actual fun Projects(
    projects: List<Project>
) {
    LazyVerticalGrid (
        columns = GridCells.Adaptive(
            minSize = 325.dp
        ),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 16.dp,
            end = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            key = { project -> project.id },
            items = projects
        ) { project ->
            ProjectItem(
                project = project
            )
        }
    }
}

/**
 * Function to navigate to the section to allow the project creation or editing
 *
 * @param project: the project to edit if passed
 */
actual fun workOnProject(
    project: Project?
) {
    MainScope().launch {
        val entry = navigator.currentEntry.first()
        entry?.stateHolder?.set(PROJECT_KEY, project)
    }
    navigator.navigate(WORK_ON_PROJECT_DIALOG)
}

/**
 * Function to navigate to the section where the user can join in a project
 *
 * No-any params required
 */
actual fun joinProject() {
    navigator.navigate(JOIN_PROJECT_DIALOG)
}

/**
 * Function to manage correctly the back navigation from the current screen
 *
 * No-any params required
 */
@Composable
@NonRestartableComposable
actual fun CloseApplicationOnNavBack() {
}