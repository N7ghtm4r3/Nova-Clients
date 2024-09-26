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
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
    MainScope().launch {
        navigator.currentEntry.collect { entry ->
            entry!!.stateHolder[PROJECT_KEY] = project
        }
    }
    navigator.navigate(WORK_ON_PROJECT_SCREEN)
}