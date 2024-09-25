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
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.novacore.records.project.Project

@NonRestartableComposable
@Composable
actual fun Releases(
    paddingValues: PaddingValues,
    project: Project
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
                release = release
            )
        }
    }
}