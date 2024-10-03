package com.tecknobit.nova.ui.screens.addmembers

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
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

/**
 * The **AddMembersScreenViewModel** class is the support class used by the [AddMembersScreen] to
 * generate the qrcode and the request to add members to the [project]
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 * @param project: the project where the user have to be added
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 * @see EquinoxViewModel
 *
 */
class AddMembersScreenViewModel(
    snackbarHostState: SnackbarHostState,
    val project: Project
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **members** -> the members list with their role
     */
    lateinit var members: SnapshotStateList<Pair<String, Role>>

    /**
     * **qrcodeCreationAccepted** -> state to manage the qrcode creation
     */
    lateinit var qrcodeCreationAccepted: MutableState<Boolean>

    /**
     * **creatingQRCode** -> state to manage the qrcode creating phase
     */
    lateinit var creatingQRCode: MutableState<Boolean>

    /**
     * **errorDuringCreation** -> state to manage an error occurred during the qrcode creating phase
     */
    lateinit var errorDuringCreation: MutableState<Boolean>

    /**
     * **joiningQRCode** -> the created joining qrcode to share or to scan
     */
    var joiningQRCode: JoiningQRCode = JoiningQRCode()

    /**
     * Function to add an empty record to the [members] list
     *
     * No-any params required
     */
    fun addEmptyItem() {
        members.add(Pair("", Role.Customer))
    }

    /**
     * Function to fill the details of a member to add
     *
     * @param index: the index of [members] list where the details must be used
     * @param email: the email of the member
     * @param role: the role of the member
     */
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

    /**
     * Function to execute the request to add new members to the [project] and the related qrcode
     * creation
     *
     * No-any params hurried
     */
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

    /**
     * Function to get the qrcode data to share
     *
     * No-any params required
     *
     * @return the qrcode data as [String]
     */
    fun getQRCodeData(): String {
        return JSONObject()
            .put(HOST_ADDRESS_KEY, activeLocalSession.hostAddress)
            .put(IDENTIFIER_KEY, joiningQRCode.id)
            .put(JOIN_CODE_KEY, joiningQRCode.joinCode)
            .toString()
    }

}