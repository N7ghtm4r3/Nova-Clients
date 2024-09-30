package com.tecknobit.nova.ui.screens.addmembers

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.HOST_ADDRESS_KEY
import com.tecknobit.novacore.records.NovaUser.IDENTIFIER_KEY
import com.tecknobit.novacore.records.NovaUser.Role
import com.tecknobit.novacore.records.project.JoiningQRCode
import com.tecknobit.novacore.records.project.JoiningQRCode.JOIN_CODE_KEY
import com.tecknobit.novacore.records.project.Project
import org.json.JSONObject

class AddMembersScreenViewModel(
    snackbarHostState: SnackbarHostState,
    val project: Project
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var members: SnapshotStateList<Pair<String, Role>>

    lateinit var qrcodeCreationAccepted: MutableState<Boolean>

    lateinit var creatingQRCode: MutableState<Boolean>

    lateinit var errorDuringCreation: MutableState<Boolean>

    var joiningQRCode: JoiningQRCode = JoiningQRCode()

    fun addEmptyItem() {
        members.add(Pair("", Role.Customer))
    }

    fun addMember(
        index: Int,
        email: String,
        role: Role
    ) {
        members[index] = Pair(email, role)
    }

    /**
     * Function to remove an item from a support list
     *
     * @param index: the index of the member to remove
     */
    fun removeMember(
        index: Int
    ) {
        members.removeAt(index)
    }

    fun addMembers() {
        requester.sendRequest(
            request = {
                qrcodeCreationAccepted.value = true
                requester.addMembers(
                    projectId = project.id,
                    invitedMembers = members
                )
            },
            onSuccess = { response ->
                joiningQRCode = JoiningQRCode(response.jsonObjectSource)
                creatingQRCode.value = false
            },
            onFailure = { errorDuringCreation.value = true }
        )
    }

    fun getQRCodeData(): String {
        return JSONObject()
            .put(HOST_ADDRESS_KEY, activeLocalSession.hostAddress)
            .put(IDENTIFIER_KEY, joiningQRCode.id)
            .put(JOIN_CODE_KEY, joiningQRCode.joinCode)
            .toString()
    }

}