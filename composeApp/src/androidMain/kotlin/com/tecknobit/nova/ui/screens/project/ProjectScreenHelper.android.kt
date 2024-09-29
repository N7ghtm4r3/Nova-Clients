package com.tecknobit.nova.ui.screens.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.navigator
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.ADD_MEMBERS_SCREEN
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import com.tecknobit.novacore.records.release.Release
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@NonRestartableComposable
@Composable
actual fun Releases(
    paddingValues: PaddingValues,
    project: Project,
    onEdit: (Release) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding()
            )
            .fillMaxSize()
            .background(gray_background),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            key = { release -> release.id },
            items = project.releases
        ) { release ->
            ReleaseItem(
                project = project,
                release = release,
                onEdit = onEdit
            )
        }
    }
}

actual fun addMembers(
    project: Project
) {
    MainScope().launch {
        val entry = navigator.currentEntry.first()
        entry?.stateHolder?.set(PROJECT_KEY, project)
    }
    navigator.navigate(ADD_MEMBERS_SCREEN)
}