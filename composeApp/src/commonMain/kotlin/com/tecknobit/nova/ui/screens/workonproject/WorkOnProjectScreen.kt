@file:OptIn(ExperimentalMaterial3Api::class)

package com.tecknobit.nova.ui.screens.workonproject

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.graphics.Color
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.theme.gray_background
import com.tecknobit.novacore.records.project.Project

/**
 * The [WorkOnProjectScreen] class is used to add a new project or edit an existing project
 *
 * @param project: the project to edit if passed
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxScreen
 * @see NovaScreen
 * @see WorkOnProject
 */
class WorkOnProjectScreen(
    project: Project?
) : WorkOnProject(
    project = project
) {

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        super.ArrangeScreenContent()
        Scaffold(
            containerColor = gray_background,
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        NavBackButton()
                    },
                    title = { ScreenTitle() },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        viewModel.workOnProject(
                            project = project,
                            onSuccess = { navigator.goBack() }
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null
                    )
                }
            }
        ) { paddingValues ->
            ProjectForm(
                paddingValues = paddingValues
            )
        }
    }

    /**
     * Function to arrange correctly the [PotentialMembers] section for each platform
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    override fun ProjectMembersSectionsImpl() {
        PotentialMembers()
    }

}