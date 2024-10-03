package com.tecknobit.nova.ui.screens.projects

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.JOIN_PROJECT_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.WORK_ON_PROJECT_SCREEN
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.BackHandler

/**
 * Component to display and arrange correctly from each platform the projects in the UI
 *
 * @param projects: the projects list to display
 */
@Composable
actual fun Projects(
    projects: List<Project>
) {
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
    navigator.navigate(WORK_ON_PROJECT_SCREEN)
}

/**
 * Function to navigate to the section where the user can join in a project
 *
 * No-any params required
 */
actual fun joinProject() {
    navigator.navigate(JOIN_PROJECT_SCREEN)
}

/**
 * Function to manage correctly the back navigation from the current screen
 *
 * No-any params required
 */
@Composable
@NonRestartableComposable
actual fun CloseApplicationOnNavBack() {
    val context = LocalContext.current as Activity
    BackHandler {
        context.finishAffinity()
    }
}