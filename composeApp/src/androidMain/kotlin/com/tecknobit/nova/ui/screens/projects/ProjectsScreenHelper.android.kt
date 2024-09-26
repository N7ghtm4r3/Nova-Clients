package com.tecknobit.nova.ui.screens.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.WORK_ON_PROJECT_SCREEN
import com.tecknobit.novacore.records.project.Project

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

actual fun workOnProject(
    project: Project?
) {
    val projectId = if (project != null)
        "/${project.id}"
    else
        ""
    navigator.navigate(WORK_ON_PROJECT_SCREEN + projectId)
}