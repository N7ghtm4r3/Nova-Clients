@file:OptIn(ExperimentalMaterial3Api::class)

package com.tecknobit.nova.ui.screens.workonproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.theme.gray_background
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.confirm
import org.jetbrains.compose.resources.stringResource

/**
 * The [WorkOnProjectDialog] class is used to add a new project or edit an existing project
 *
 * @param project: the project to edit if passed
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxScreen
 * @see NovaScreen
 * @see WorkOnProject
 */
class WorkOnProjectDialog(
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
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = gray_background
                    ),
                    navigationIcon = {
                        NavBackButton(
                            tint = LocalContentColor.current
                        )
                    },
                    title = { ScreenTitle() }
                )
            },
            containerColor = gray_background
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
        Column {
            PotentialMembers(
                modifier = Modifier
                    .weight(3f)
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp
                ),
                onClick = {
                    viewModel.workOnProject(
                        project = project,
                        onSuccess = { navigator.goBack() }
                    )
                }
            ) {
                Text(
                    text = stringResource(Res.string.confirm),
                    fontSize = 18.sp
                )
            }
        }
    }

}