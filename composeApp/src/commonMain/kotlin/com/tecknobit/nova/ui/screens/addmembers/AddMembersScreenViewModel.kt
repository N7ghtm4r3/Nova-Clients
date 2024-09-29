package com.tecknobit.nova.ui.screens.addmembers

import androidx.compose.material3.SnackbarHostState
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.novacore.records.project.Project

class AddMembersScreenViewModel(
    snackbarHostState: SnackbarHostState,
    project: Project
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
)