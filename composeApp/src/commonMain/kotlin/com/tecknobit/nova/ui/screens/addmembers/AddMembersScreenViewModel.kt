package com.tecknobit.nova.ui.screens.addmembers

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.records.NovaUser.Role
import com.tecknobit.novacore.records.project.Project

class AddMembersScreenViewModel(
    snackbarHostState: SnackbarHostState,
    val project: Project
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    val members: SnapshotStateList<Pair<String, Role>> = mutableStateListOf()

    fun addMembers() {
        requester.sendRequest(
            request = {
                requester.addMembers(
                    projectId = project.id,
                    mailingList = members
                )
            },
            onSuccess = { navigator.goBack() },
            onFailure = { showSnackbarMessage(it) }
        )
    }

}