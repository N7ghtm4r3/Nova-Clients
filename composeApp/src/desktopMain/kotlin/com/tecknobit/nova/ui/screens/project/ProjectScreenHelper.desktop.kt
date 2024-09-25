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
import com.tecknobit.novacore.records.project.Project

@NonRestartableComposable
@Composable
actual fun Releases(
    paddingValues: PaddingValues,
    project: Project
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
                release = release
            )
        }
    }
}