package com.tecknobit.nova.ui.screens.joinproject

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY
import com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY
import com.tecknobit.equinox.inputs.InputValidator.HOST_ADDRESS_KEY
import com.tecknobit.equinox.inputs.InputValidator.isHostValid
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.helpers.utils.NovaRequester
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.records.NovaUser.PROFILE_PIC_KEY
import com.tecknobit.novacore.records.NovaUser.ROLE_KEY
import com.tecknobit.novacore.records.NovaUser.Role

/**
 * The **JoinProjectScreenViewModel** class is the support class used by the [JoinProjectScreen] to execute
 * the requests to join in a project
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 * @see EquinoxViewModel
 *
 */
class JoinProjectScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **host** -> the value of the host to reach
     */
    lateinit var host: MutableState<String>

    /**
     * **hostError** -> whether the [host] field is not valid
     */
    lateinit var hostError: MutableState<Boolean>

    /**
     * Function to execute the request to join in a project by a scan a join code
     *
     * @param content: the content scanned by the qrcode scanner
     */
    fun joinWithScannedQR(
        content: String
    ) {
        val helper = JsonHelper(content)
        val joinQRCodeId = helper.getString(IDENTIFIER_KEY, null)
        val hostAddress = helper.getString(HOST_ADDRESS_KEY, null)
        if (joinQRCodeId == null && hostAddress == null) {
            showSnackbarMessage(helper)
            return
        }
        val requester = NovaRequester(
            host = hostAddress,
            userId = activeLocalSession.id,
            userToken = activeLocalSession.token,
        )
        requester.sendRequest(
            request = {
                requester.joinWithId(
                    id = joinQRCodeId,
                    email = activeLocalSession.email,
                    name = activeLocalSession.name,
                    surname = activeLocalSession.surname,
                    password = activeLocalSession.password,
                    role = activeLocalSession.role
                )
            },
            onSuccess = { response ->
                manageResponse(
                    response = response,
                    hostAddress = hostAddress
                )
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    /**
     * Function to execute the request to join in a project by a join code
     *
     * @param joinCode: the join code to use
     */
    fun joinWithCode(
        joinCode: String
    ) {
        if (!isHostValid(host.value)) {
            hostError.value = true
            return
        }
        val requester = NovaRequester(
            host = host.value,
            userId = activeLocalSession.id,
            userToken = activeLocalSession.token,
        )
        requester.sendRequest(
            request = {
                requester.joinWithCode(
                    joinCode = joinCode,
                    email = activeLocalSession.email,
                    name = activeLocalSession.name,
                    surname = activeLocalSession.surname,
                    password = activeLocalSession.password,
                    role = activeLocalSession.role
                )
            },
            onSuccess = { response ->
                manageResponse(
                    response = response,
                    hostAddress = host.value
                )
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    /**
     * Function to manage the response of a join request
     *
     * @param response: the response received
     * @param hostAddress: the host address to use in the session
     */
    private fun manageResponse(
        response: JsonHelper,
        hostAddress: String
    ) {
        val payloadResponse = response.getJSONObject(RESPONSE_MESSAGE_KEY)
        val userIdentifier = response.getString(IDENTIFIER_KEY)
        val token = response.getString(TOKEN_KEY)
        if (payloadResponse.has(TOKEN_KEY)) {
            localSessionsHelper.insertSession(
                userIdentifier,
                token,
                response.getString(PROFILE_PIC_KEY),
                activeLocalSession.name,
                activeLocalSession.surname,
                activeLocalSession.email,
                activeLocalSession.password,
                hostAddress,
                Role.valueOf(response.getString(ROLE_KEY)),
                activeLocalSession.language
            )
        } else if (activeLocalSession.id != userIdentifier)
            localSessionsHelper.changeActiveSession(userIdentifier)
        activeLocalSession = localSessionsHelper.activeSession!!
        requester.changeHost(
            host = hostAddress
        )
        requester.setUserCredentials(
            userId = activeLocalSession.id,
            userToken = activeLocalSession.token
        )
        navigator.goBack()
    }

}