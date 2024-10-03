package com.tecknobit.nova.ui.screens.auth

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY
import com.tecknobit.equinox.environment.records.EquinoxUser.*
import com.tecknobit.equinox.inputs.InputValidator.*
import com.tecknobit.equinoxcompose.helpers.viewmodels.EquinoxViewModel
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.requester
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.LOGGED_AS_CUSTOMER_RECORD_VALUE
import com.tecknobit.novacore.records.NovaUser.*
import com.tecknobit.novacore.records.NovaUser.Role.Vendor
import java.util.*

/**
 * The **AuthScreenViewModel** class is the support class used by the [AuthScreen] to execute
 * the authentication requests
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ViewModel
 * @see FetcherManagerWrapper
 * @see EquinoxViewModel
 *
 */
class AuthScreenViewModel(
    snackbarHostState: SnackbarHostState,
) : EquinoxViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **isSignUp** -> whether the auth request to execute is sign up or sign in
     */
    lateinit var isSignUp: MutableState<Boolean>

    /**
     * **isCustomerAuth** -> whether the auth request is to execute a customer authentication
     */
    lateinit var isCustomerAuth: MutableState<Boolean>

    /**
     * **host** -> the value of the host to reach
     */
    lateinit var host: MutableState<String>

    /**
     * **hostError** -> whether the [host] field is not valid
     */
    lateinit var hostError: MutableState<Boolean>

    /**
     * **serverSecret** -> the value of the server secret
     */
    lateinit var serverSecret: MutableState<String>

    /**
     * **serverSecretError** -> whether the [serverSecret] field is not valid
     */
    lateinit var serverSecretError: MutableState<Boolean>

    /**
     * **name** -> the name of the user
     */
    lateinit var name: MutableState<String>

    /**
     * **nameError** -> whether the [name] field is not valid
     */
    lateinit var nameError: MutableState<Boolean>

    /**
     * **surname** -> the surname of the user
     */
    lateinit var surname: MutableState<String>

    /**
     * **surnameError** -> whether the [surname] field is not valid
     */
    lateinit var surnameError: MutableState<Boolean>

    /**
     * **email** -> the email of the user
     */
    lateinit var email: MutableState<String>

    /**
     * **emailError** -> whether the [email] field is not valid
     */
    lateinit var emailError: MutableState<Boolean>

    /**
     * **password** -> the password of the user
     */
    lateinit var password: MutableState<String>

    /**
     * **passwordError** -> whether the [password] field is not valid
     */
    lateinit var passwordError: MutableState<Boolean>

    /**
     * Wrapper function to execute the specific authentication request
     *
     * No-any params required
     */
    fun auth() {
        if (isSignUp.value) {
            if(signUpFormIsValid())
                signUp()
        } else {
            if(signInFormIsValid())
                signIn()
        }
    }

    /**
     * Function to execute the sign-up authentication request, if successful the [activeLocalSession]
     * will be initialized with the data received by the request
     *
     * No-any params required
     */
    private fun signUp() {
        if (signUpFormIsValid()) {
            val language = getUserLanguage()
            if(isCustomerAuth.value) {
                customerSignUp(
                    language = language
                )
            } else {
                vendorSignUp(
                    language = language
                )
            }
        }
    }

    /**
     * Function to execute the sign-up authentication request as [Role.Customer],
     * if successful the [activeLocalSession] will be initialized with the data inserted
     *
     * @param language: the language of the user
     */
    private fun customerSignUp(
        language: String
    ) {
        localSessionsHelper.insertSession(
            id = UUID.randomUUID().toString().replace("-", ""),
            token = LOGGED_AS_CUSTOMER_RECORD_VALUE,
            profilePicUrl = LOGGED_AS_CUSTOMER_RECORD_VALUE,
            name = name.value,
            surname = surname.value,
            email = email.value,
            password = password.value,
            hostAddress = LOGGED_AS_CUSTOMER_RECORD_VALUE,
            role = Role.Customer,
            language = language
        )
        activeLocalSession = localSessionsHelper.activeSession!!
        navigator.navigate(PROJECTS_SCREEN)
    }

    /**
     * Function to execute the sign-up authentication request as [Role.Vendor],
     * if successful the [activeLocalSession] will be initialized with the data received
     *
     * @param language: the language of the user
     */
    private fun vendorSignUp(
        language: String
    ) {
        requester.changeHost(host.value)
        requester.sendRequest(
            request = {
                requester.signUp(
                    serverSecret = serverSecret.value,
                    name = name.value,
                    surname = surname.value,
                    email = email.value,
                    password = password.value,
                    language = language
                )
            },
            onSuccess = { response ->
                launchApp(
                    name = name.value,
                    surname = surname.value,
                    language = language,
                    role = Vendor,
                    response = response
                )
            },
            onFailure = { showSnackbarMessage(it) }
        )
    }

    /**
     * Function to get the current user language
     *
     * No-any params required
     *
     * @return the user language as [String]
     */
    private fun getUserLanguage(): String {
        val currentLanguageTag = getValidUserLanguage()
        val language = LANGUAGES_SUPPORTED[currentLanguageTag]
        return if (language == null)
            DEFAULT_LANGUAGE
        else
            currentLanguageTag
    }

    /**
     * Function to validate the inputs for the [signUp] request
     *
     * No-any params required
     *
     * @return whether the inputs are valid as [Boolean]
     */
    protected open fun signUpFormIsValid(): Boolean {
        var isValid: Boolean
        if (!isCustomerAuth.value) {
            isValid = isHostValid(host.value)
            if (!isValid) {
                hostError.value = true
                return false
            }
            isValid = isServerSecretValid(serverSecret.value)
            if (!isValid) {
                serverSecretError.value = true
                return false
            }
        }
        isValid = isNameValid(name.value)
        if (!isValid) {
            nameError.value = true
            return false
        }
        isValid = isSurnameValid(surname.value)
        if (!isValid) {
            surnameError.value = true
            return false
        }
        isValid = isEmailValid(email.value)
        if (!isValid) {
            emailError.value = true
            return false
        }
        isValid = isPasswordValid(password.value)
        if (!isValid) {
            passwordError.value = true
            return false
        }
        return true
    }

    /**
     * Function to execute the sign in authentication request, if successful the [localUser] will
     * be initialized with the data received by the request
     *
     * No-any params required
     */
    private fun signIn() {
        if (signInFormIsValid()) {
            requester.changeHost(host.value)
            requester.sendRequest(
                request = {
                    requester.signIn(
                        email = email.value,
                        password = password.value
                    )
                },
                onSuccess = { response ->
                    launchApp(
                        name = response.getString(NAME_KEY),
                        surname = response.getString(SURNAME_KEY),
                        language = response.getString(LANGUAGE_KEY),
                        role = Role.valueOf(response.getString(ROLE_KEY)),
                        response = response
                    )
                },
                onFailure = { showSnackbarMessage(it) }
            )
        }
    }

    /**
     * Function to validate the inputs for the [signIn] request
     *
     * No-any params required
     *
     * @return whether the inputs are valid as [Boolean]
     */
    protected open fun signInFormIsValid(): Boolean {
        var isValid: Boolean = isHostValid(host.value)
        if (!isValid) {
            hostError.value = true
            return false
        }
        isValid = isEmailValid(email.value)
        if (!isValid) {
            emailError.value = true
            return false
        }
        isValid = isPasswordValid(password.value)
        if (!isValid) {
            passwordError.value = true
            return false
        }
        return true
    }

    /**
     * Function to launch the application after the authentication request, will be instantiated with the user details
     * both the [requester] and the [localUser]
     *
     * @param response: the response of the authentication request
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param language: the language of the user
     * @param custom: the custom parameters added in a customization of the [EquinoxUser]
     */
    protected open fun launchApp(
        response: JsonHelper,
        name: String,
        surname: String,
        language: String,
        role: Role
    ) {
        requester.setUserCredentials(
            userId = response.getString(IDENTIFIER_KEY),
            userToken = response.getString(TOKEN_KEY)
        )
        localSessionsHelper.insertSession(
            id = response.getString(IDENTIFIER_KEY),
            token = response.getString(TOKEN_KEY),
            profilePicUrl = response.getString(PROFILE_PIC_KEY),
            name = name,
            surname = surname,
            email = email.value,
            password = password.value,
            hostAddress = host.value,
            role = role,
            language = language
        )
        activeLocalSession = localSessionsHelper.activeSession!!
        navigator.navigate(PROJECTS_SCREEN)
    }

}