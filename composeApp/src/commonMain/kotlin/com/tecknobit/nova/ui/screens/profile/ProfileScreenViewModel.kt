package com.tecknobit.nova.ui.screens.profile

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.equinox.inputs.InputValidator.isEmailValid
import com.tecknobit.equinox.inputs.InputValidator.isPasswordValid
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession
import com.tecknobit.novacore.records.NovaUser.PROFILE_PIC_KEY
import java.io.File

class ProfileScreenViewModel(
    snackbarHostState: SnackbarHostState
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **newEmail** -> the value of the new email to set
     */
    lateinit var newEmail: MutableState<String>

    /**
     * **newEmailError** -> whether the [newEmail] field is not valid
     */
    lateinit var newEmailError: MutableState<Boolean>

    /**
     * **newPassword** -> the value of the new password to set
     */
    lateinit var newPassword: MutableState<String>

    /**
     * **newPasswordError** -> whether the [newPassword] field is not valid
     */
    lateinit var newPasswordError: MutableState<Boolean>

    /**
     * Function to execute the profile pic change
     *
     * @param imagePath: the path of the image to set
     * @param profilePic: the state used to display the current profile pic
     */
    fun changeProfilePic(
        imagePath: String?,
        profilePic: MutableState<String>
    ) {
        if(imagePath != null) {
            requester.sendRequest(
                request = {
                    requester.changeProfilePic(
                        profilePic = File(imagePath)
                    )
                },
                onSuccess = { response ->
                    localSessionsHelper.changeProfilePic(response.getString(PROFILE_PIC_KEY))
                    activeLocalSession = localSessionsHelper.activeSession!!
                    profilePic.value = activeLocalSession.profilePicUrl
                },
                onFailure = { showSnackbarMessage(it) }
            )
        }
    }

    /**
     * Function to execute the email change
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
    fun changeEmail(
        onSuccess: () -> Unit
    ) {
        if (isEmailValid(newEmail.value)) {
            requester.sendRequest(
                request = {
                    requester.changeEmail(
                        newEmail = newEmail.value
                    )
                },
                onSuccess = {
                    localSessionsHelper.changeEmail(newEmail.value)
                    activeLocalSession.email = newEmail.value
                    onSuccess.invoke()
                },
                onFailure = { showSnackbarMessage(it) }
            )
        } else
            newEmailError.value = true
    }

    /**
     * Function to execute the password change
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
    fun changePassword(
        onSuccess: () -> Unit
    ) {
        if (isPasswordValid(newPassword.value)) {
            requester.sendRequest(
                request = {
                    requester.changePassword(
                        newPassword = newPassword.value
                    )
                },
                onSuccess = {
                    localSessionsHelper.changePassword(newPassword.value)
                    onSuccess.invoke()
                },
                onFailure = { showSnackbarMessage(it) }
            )
        } else
            newPasswordError.value = true
    }

    /**
     * Function to execute the language change
     *
     * @param newLanguage: the new language of the user
     * @param onSuccess: the action to execute if the request has been successful
     */
    fun changeLanguage(
        newLanguage: String,
        onSuccess: () -> Unit
    ) {
        var languageKey: String = DEFAULT_LANGUAGE
        LANGUAGES_SUPPORTED.forEach {
            if(it.value == newLanguage) {
                languageKey = it.key
                return@forEach
            }
        }
        requester.sendRequest(
            request = {
                requester.changeLanguage(
                    newLanguage = languageKey
                )
            },
            onSuccess = {
                localSessionsHelper.changeLanguage(newLanguage)
                activeLocalSession.language = newLanguage
                onSuccess.invoke()
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    // TODO: TO COMMENT
    fun logout(
        onLogout : () -> Unit
    ) {
        localSessionsHelper.deleteAllSessions()
        requester.setUserCredentials(null, null)
        onLogout.invoke()
    }

    /**
     * Function to execute the account deletion
     *
     * @param onDelete: the action to execute when the account has been deleted
     */
    fun deleteAccount(
        onDelete: () -> Unit,
        onFailure: () -> Unit
    ) {
        val postDeletion = {
            localSessionsHelper.deleteSession(activeLocalSession.id)
            val sessions = localSessionsHelper.sessions
            if(sessions.isNotEmpty())
                localSessionsHelper.changeActiveSession(sessions.first().id)
            onDelete.invoke()
        }
        if(activeLocalSession.isHostSet) {
            requester.sendRequest(
                request = { requester.deleteAccount() },
                onSuccess = {
                    requester.setUserCredentials(null, null)
                    postDeletion.invoke()
                },
                onFailure = { onFailure.invoke() }
            )
        } else
            postDeletion.invoke()
    }

    fun changeCurrentLocalSession(
        session: NovaSession,
        onChange: () -> Unit
    ) {
        localSessionsHelper.changeActiveSession(session.id)
        onChange.invoke()
    }

}