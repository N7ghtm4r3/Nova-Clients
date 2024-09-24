package com.tecknobit.nova.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.equinoxcompose.components.EquinoxAlertDialog
import com.tecknobit.nova.imageLoader
import com.tecknobit.nova.navigator
import com.tecknobit.nova.screens.NovaScreen
import com.tecknobit.nova.screens.SplashScreen.Companion.activeLocalSession
import com.tecknobit.nova.screens.SplashScreen.Companion.localSessionsHelper
import com.tecknobit.nova.screens.SplashScreen.Companion.requester
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.theme.tagstheme.bug.md_theme_light_primary
import com.tecknobit.nova.thinFontFamily
import nova.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class ProfileScreen : NovaScreen() {

    val viewModel = ProfileScreenViewModel()

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        Card (
            modifier = Modifier
                .size(
                    width = 425.dp,
                    height = 700.dp
                ),
            colors = CardDefaults.cardColors(
                containerColor = gray_background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(225.dp)
                            .clickable (
                                enabled = activeLocalSession.isHostSet
                            ) { /*pickProfilePic = true*/ },
                        imageLoader = imageLoader,
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            //.data(profilePic)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    /*FilePicker(
                        show = pickProfilePic,
                        fileExtensions = fileType
                    ) { projectLogoUri ->
                        if(projectLogoUri != null) {
                            requester.sendRequest(
                                request = {
                                    requester.changeProfilePic(
                                        profilePic = File(projectLogoUri.path)
                                    )
                                },
                                onSuccess = { response ->
                                    localSessionsHelper.changeProfilePic(response.getString(User.PROFILE_PIC_URL_KEY))
                                    activeLocalSession = localSessionsHelper.activeSession
                                    profilePic = activeLocalSession.profilePicUrl
                                    pickProfilePic = false
                                    showProfile.value = true
                                },
                                onFailure = {
                                    pickProfilePic = false
                                    showProfile.value = true
                                }
                            )
                        }
                    }*/
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = 5.dp
                            ),
                        onClick = { navigator.goBack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                start = 10.dp,
                                bottom = 5.dp
                            ),
                        text = "${activeLocalSession.name} ${activeLocalSession.surname}",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
                UserInfo(
                    header = Res.string.uid,
                    info = activeLocalSession.id
                )
                /*UserInfo(
                    header = Res.string.email,
                    editAction = { /*showChangeEmail.value = true*/ },
                    info = currentEmail
                )*/
                /*if(showChangeEmail.value) {
                    val email = remember { mutableStateOf("") }
                    val emailError = remember { mutableStateOf(false) }
                    val emailErrorMessage = remember { mutableStateOf("") }
                    NovaAlertDialog(
                        show = showChangeEmail,
                        icon = Icons.Default.Email,
                        title = stringResource(Res.string.change_email),
                        message = {
                            Column {
                                Text(
                                    text = stringResource(Res.string.please_enter_the_new_email_address)
                                )
                                EquinoxTextField(
                                    value = email,
                                    label = Res.string.email,
                                    keyboardType = KeyboardType.Email,
                                    errorMessage = emailErrorMessage,
                                    isError = emailError
                                )
                            }
                        },
                        confirmAction = {
                            /*if(isEmailValid(email.value)) {
                                email.value = email.value.lowercase()
                                requester.sendRequest(
                                    request = {
                                        requester.changeEmail(
                                            newEmail = email.value
                                        )
                                    },
                                    onSuccess = {
                                        localSessionsHelper.changeEmail(email.value)
                                        currentEmail = email.value
                                        showChangeEmail.value = false
                                    },
                                    onFailure = {
                                        showChangeEmail.value = false
                                        setErrorMessage(
                                            errorMessage = emailErrorMessage,
                                            errorMessageKey = Res.string.wrong_email,
                                            error = emailError
                                        )
                                    }
                                )
                            } else {
                                setErrorMessage(
                                    errorMessage = emailErrorMessage,
                                    errorMessageKey = Res.string.wrong_email,
                                    error = emailError
                                )
                            }*/
                        }
                    )
                }
                UserInfo(
                    header = Res.string.password,
                    editAction = { showChangePassword.value = true },
                    info = userPassword,
                    onInfoClick = {
                        userPassword = if(userPassword == PASSWORD_HIDDEN)
                            activeLocalSession.password
                        else
                            PASSWORD_HIDDEN
                    }
                )
                if(showChangePassword.value) {
                    val password = remember { mutableStateOf("") }
                    val passwordErrorMessage = remember { mutableStateOf("") }
                    val passwordError = remember { mutableStateOf(false) }
                    var isPasswordHidden by remember { mutableStateOf(true) }
                    NovaAlertDialog(
                        show = showChangePassword,
                        icon = Icons.Default.Password,
                        title = stringResource(Res.string.change_password),
                        message = {
                            Column {
                                Text(
                                    text = stringResource(Res.string.please_enter_the_new_password)
                                )
                                EquinoxTextField(
                                    value = password,
                                    visualTransformation = if (isPasswordHidden)
                                        PasswordVisualTransformation()
                                    else
                                        VisualTransformation.None,
                                    leadingIcon = {
                                        IconButton(
                                            onClick = { password.value = "" }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    label = Res.string.password,
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { isPasswordHidden = !isPasswordHidden }
                                        ) {
                                            Icon(
                                                imageVector = if(isPasswordHidden)
                                                    Icons.Default.Visibility
                                                else
                                                    Icons.Default.VisibilityOff,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    keyboardType = KeyboardType.Password,
                                    errorMessage = passwordErrorMessage,
                                    isError = passwordError
                                )
                            }
                        },
                        confirmAction = {
                            /*if(isPasswordValid(password.value)) {
                                requester.sendRequest(
                                    request = {
                                        requester.changePassword(
                                            newPassword = password.value
                                        )
                                    },
                                    onSuccess = {
                                        localSessionsHelper.changePassword(password.value)
                                        if(userPassword != PASSWORD_HIDDEN)
                                            userPassword = password.value
                                        activeLocalSession.password = password.value
                                        showChangePassword.value = false
                                    },
                                    onFailure = {
                                        setErrorMessage(
                                            errorMessage = passwordErrorMessage,
                                            errorMessageKey = Res.string.wrong_password,
                                            error = passwordError
                                        )
                                        showChangePassword.value = false
                                    }
                                )
                            } else {
                                setErrorMessage(
                                    errorMessage = passwordErrorMessage,
                                    errorMessageKey = Res.string.wrong_password,
                                    error = passwordError
                                )
                            }*/
                        }
                    )
                }
                UserInfo(
                    header = Res.string.language,
                    editAction = { showChangeLanguage.value = true },
                    info = activeLocalSession.language,
                    isLast = true
                )
                if(showChangeLanguage.value) {
                    var selectedLanguage by remember { mutableStateOf(activeLocalSession.language) }
                    EquinoxAlertDialog(
                        show = showChangeLanguage,
                        icon = Icons.Default.Language,
                        title = stringResource(Res.string.change_language),
                        message = {
                            Column {
                                LazyColumn (
                                    modifier = Modifier
                                        .height(150.dp)
                                ) {
                                    items(
                                        key = { it },
                                        items = LANGUAGES_SUPPORTED.values.toList()
                                    ) { language ->
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = language == selectedLanguage,
                                                onClick = { selectedLanguage = language }
                                            )
                                            Text(
                                                text = language
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmAction = {

                            viewModel.cha

                            requester.sendRequest(
                                request = {
                                    requester.changeLanguage(
                                        newLanguage = selectedLanguage
                                    )
                                },
                                onSuccess = {
                                    localSessionsHelper.changeLanguage(selectedLanguage)
                                    activeLocalSession.language = selectedLanguage
                                    showChangeLanguage.value = false
                                    navToSplashscreen()
                                },
                                onFailure = { showChangeLanguage.value = false }
                            )
                        }
                    )
                }
                if(mySessions.size > 1) {
                    Text(
                        modifier = Modifier
                            .padding(
                                start = 10.dp,
                                top = 5.dp
                            ),
                        text = stringResource(Res.string.my_sessions),
                        fontSize = 22.sp
                    )
                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(
                            top = 5.dp,
                            start = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp
                        )
                    ) {
                        items(
                            key = { it.id },
                            items = mySessions
                        ) { session ->
                            val isCurrentSession = session.isActive
                            ListItem(
                                modifier = Modifier
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .clickable(!isCurrentSession) {
                                        showProfile.value = false
                                        localSessionsHelper.changeActiveSession(session.id)
                                        navigator.navigate(SPLASH_SCREEN_ROUTE)
                                    }
                                    .clip(RoundedCornerShape(15.dp)),
                                colors = ListItemDefaults.colors(
                                    containerColor = Color.White
                                ),
                                leadingContent = {
                                    Logo(
                                        url = session.profilePicUrl,
                                        size = 85.dp
                                    )
                                },
                                overlineContent = {
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        UserRoleBadge(
                                            role = session.role,
                                            background = Color.White
                                        )
                                        if(isCurrentSession) {
                                            Text(
                                                text = stringResource(Res.string.current)
                                            )
                                        }
                                    }
                                },
                                headlineContent = {
                                    Text(
                                        text = session.hostAddress
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        text = session.email,
                                        fontSize = 13.sp
                                    )
                                },
                                trailingContent = {
                                    if(!isCurrentSession) {
                                        IconButton(
                                            onClick = {
                                                localSessionsHelper.deleteSession(session.id)
                                                mySessions.remove(session)
                                            }
                                        ) {
                                            Icon (
                                                modifier = Modifier
                                                    .size(20.dp),
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            )
                        }
                        item {
                            ActionButtons(
                                spaceButtons = false
                            )
                        }
                    }
                } else
                    ActionButtons()*/
            }
        }
    }

    override fun onStart() {
        viewModel.setActiveContext(this::class.java)
    }

    /**
     * Function to display a specific [activeLocalSession] detail
     *
     * @param header: the header of the section, so the detail displayed
     * @param editAction: the edit action to execute, if is an editable data
     * @param info: the info detail to display
     * @param onInfoClick: the action to execute when the info is clicked, for example hidden/unhidden
     * the password data
     * @param isLast: whether the section displayed is the last to display
     */
    @Composable
    private fun UserInfo(
        header: StringResource,
        editAction: (() -> Unit)? = null,
        info: String,
        onInfoClick: (() -> Unit)? = null,
        isLast: Boolean = false
    ) {
        Column (
            modifier = Modifier
                .padding(
                    start = 10.dp
                )
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ){
                Text(
                    modifier = Modifier.padding(
                        start = 5.dp
                    ),
                    text = stringResource(header),
                    fontFamily = thinFontFamily
                )
                if(editAction != null && activeLocalSession.isHostSet) {
                    Button(
                        modifier = Modifier
                            .height(25.dp),
                        onClick = editAction,
                        shape = RoundedCornerShape(5.dp),
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            end = 10.dp,
                            top = 0.dp,
                            bottom = 0.dp
                        ),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.edit),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Text(
                modifier = Modifier
                    .padding(
                        start = 5.dp
                    )
                    .clickable (onInfoClick != null) {
                        onInfoClick!!()
                    },
                text = info,
                fontSize = 17.sp
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            color = Color.LightGray
        )
        if(!isLast)
            Spacer(modifier = Modifier.height(5.dp))
    }

    /**
     * Function to create and display the action buttons section
     *
     * No-any params required
     */
    @Composable
    private fun ActionButtons(
        spaceButtons: Boolean = true
    ) {
        val logout = remember { mutableStateOf(false) }
        val deleteAccount = remember { mutableStateOf(false) }
        Column (
            modifier = if(spaceButtons) {
                Modifier
                    .padding(
                        top = 10.dp,
                        start = 10.dp,
                        end = 10.dp
                    )
            } else
                Modifier
        ) {
            ActionButton(
                action = { logout.value = true },
                text = Res.string.logout
            )
            EquinoxAlertDialog(
                show = logout,
                icon = Icons.AutoMirrored.Filled.Logout,
                title = Res.string.logout,
                text = Res.string.account_logout_message,
                onDismissAction = { logout.value = false },
                confirmAction = {
                    localSessionsHelper.deleteAllSessions()
                    requester.setUserCredentials(null, null)
                    clearAndGoBack()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            ActionButton(
                color = md_theme_light_primary,
                action = { deleteAccount.value = true },
                text = Res.string.delete_account
            )
            EquinoxAlertDialog(
                show = deleteAccount,
                icon = Icons.Default.Clear,
                title = Res.string.delete_account,
                text = Res.string.account_deletion_message,
                onDismissAction = { deleteAccount.value = false },
                confirmAction = {
                    val deleteSuccessAction = {
                        localSessionsHelper.deleteSession(activeLocalSession.id)
                        val sessions = localSessionsHelper.sessions
                        if(sessions.isNotEmpty())
                            localSessionsHelper.changeActiveSession(sessions.first().id)
                        deleteAccount.value = false
                        navToSplashscreen()
                    }
                    if(activeLocalSession.isHostSet) {
                        requester.sendRequest(
                            request = { requester.deleteAccount() },
                            onSuccess = {
                                requester.setUserCredentials(null, null)
                                deleteSuccessAction.invoke()
                            },
                            onFailure = { deleteAccount.value = false }
                        )
                    } else
                        deleteSuccessAction.invoke()
                }
            )
        }
    }

    /**
     * Function to create and display a button to execute an action like logout and delete of the account
     *
     * @param action: the action to execute when the button is clicked
     * @param text: the text to display on the button
     * @param color: the color of the button, default value [md_theme_light_primary]
     */
    @Composable
    private fun ActionButton(
        action: () -> Unit,
        text: StringResource,
        color: Color = MaterialTheme.colorScheme.primary
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(10.dp)
                ),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color
            ),
            onClick = action
        ) {
            Text(
                text = stringResource(text),
                fontSize = 18.sp
            )
        }
    }

    /**
     * Function to clear the current data displayed and nav back
     *
     * No-any params required
     */
    // TODO: TO CHECK
    private fun clearAndGoBack() {
        /*projects.clear()
        notifications.clear()
        navToSplashscreen()*/
    }

    /**
     * Function to navigate to the [Splashscreen]
     *
     * No-any params required
     */
    // TODO: TO CHECK
    private fun navToSplashscreen() {
        /*showProfile.value = false
        navigator.navigate(SPLASH_SCREEN_ROUTE)*/
    }

}