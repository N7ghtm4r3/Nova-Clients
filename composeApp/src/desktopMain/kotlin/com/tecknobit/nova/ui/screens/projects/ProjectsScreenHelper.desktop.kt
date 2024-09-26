package com.tecknobit.nova.ui.screens.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.WORK_ON_PROJECT_DIALOG
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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

actual fun workOnProject(
    project: Project?
) {
    MainScope().launch {
        navigator.currentEntry.collect { entry ->
            entry!!.stateHolder[PROJECT_KEY] = project
        }
    }
    navigator.navigate(WORK_ON_PROJECT_DIALOG)
}