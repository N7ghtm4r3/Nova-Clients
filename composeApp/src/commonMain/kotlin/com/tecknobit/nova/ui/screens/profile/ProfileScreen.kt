package com.tecknobit.nova.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.equinox.inputs.InputValidator.isEmailValid
import com.tecknobit.equinox.inputs.InputValidator.isPasswordValid
import com.tecknobit.equinoxcompose.components.EquinoxAlertDialog
import com.tecknobit.equinoxcompose.components.EquinoxOutlinedTextField
import com.tecknobit.nova.Logo
import com.tecknobit.nova.UserRoleBadge
import com.tecknobit.nova.imageLoader
import com.tecknobit.nova.navigator
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.thinFontFamily
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.localSessionsHelper
import com.tecknobit.nova.ui.screens.release.getAsset
import com.tecknobit.nova.ui.theme.tagstheme.bug.md_theme_light_primary
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import nova.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ProfileScreen : NovaScreen() {

    private val viewModel = ProfileScreenViewModel(
        snackbarHostState = snackbarHostState
    )

    private lateinit var mySessions: SnapshotStateList<NovaSession>

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        val profilePic = remember { mutableStateOf(activeLocalSession.profilePicUrl) }
        val launcher = rememberFilePickerLauncher(
            type = PickerType.Image,
            mode = PickerMode.Single,
        ) { picture ->
            val logoPath = getAsset(
                asset = picture
            )
            if (logoPath != null) {
                viewModel.changeProfilePic(
                    imagePath = logoPath,
                    profilePic = profilePic
                )
            }
        }
        mySessions = remember { mutableStateListOf() }
        Card (
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
                            ) { launcher.launch() },
                        imageLoader = imageLoader,
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(profilePic.value)
                            .crossfade(true)
                            .crossfade(500)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = painterResource(Res.drawable.logo)
                    )
                    NavBackButton(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = 5.dp
                            )
                    )
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
                EmailSection()
                PasswordSection()
                LanguageSection()
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
                                        viewModel.changeCurrentLocalSession(
                                            session = session,
                                            onChange = { navToSplashscreen() }
                                        )
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
                    ActionButtons()
            }
        }
    }

    /**
     * Function to display the section of the user's email and allowing the user to change it
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun EmailSection() {
        val showChangeEmailAlert = remember { mutableStateOf(false) }
        var userEmail by remember { mutableStateOf(activeLocalSession.email) }
        viewModel.newEmail = remember { mutableStateOf("") }
        viewModel.newEmailError = remember { mutableStateOf(false) }
        val resetEmailLayout = {
            viewModel.newEmail.value = ""
            viewModel.newEmailError.value = false
            showChangeEmailAlert.value = false
        }
        UserInfo(
            header = Res.string.email,
            editAction = { showChangeEmailAlert.value = true },
            info = userEmail
        )
        EquinoxAlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
            onDismissAction = resetEmailLayout,
            icon = Icons.Default.Email,
            show = showChangeEmailAlert,
            title = Res.string.change_email,
            text = {
                EquinoxOutlinedTextField(
                    value = viewModel.newEmail,
                    label = Res.string.email,
                    mustBeInLowerCase = true,
                    errorText = Res.string.wrong_email,
                    isError = viewModel.newEmailError,
                    validator = { isEmailValid(it) }
                )
            },
            confirmAction = {
                viewModel.changeEmail(
                    onSuccess = {
                        userEmail = viewModel.newEmail.value
                        resetEmailLayout.invoke()
                    }
                )
            },
            dismissText = Res.string.dismiss,
            confirmText = Res.string.confirm
        )
    }

    /**
     * Function to display the section of the user's password and allowing the user to change it
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun PasswordSection() {
        val showChangePasswordAlert = remember { mutableStateOf(false) }
        viewModel.newPassword = remember { mutableStateOf("") }
        viewModel.newPasswordError = remember { mutableStateOf(false) }
        val resetPasswordLayout = {
            viewModel.newPassword.value = ""
            viewModel.newPasswordError.value = false
            showChangePasswordAlert.value = false
        }
        var hiddenPassword by remember { mutableStateOf(true) }
        UserInfo(
            header = Res.string.password,
            editAction = { showChangePasswordAlert.value = true },
            info = "****"
        )
        EquinoxAlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
            onDismissAction = resetPasswordLayout,
            icon = Icons.Default.Password,
            show = showChangePasswordAlert,
            title = Res.string.change_password,
            text = {
                EquinoxOutlinedTextField(
                    value = viewModel.newPassword,
                    label = Res.string.password,
                    trailingIcon = {
                        IconButton(
                            onClick = { hiddenPassword = !hiddenPassword }
                        ) {
                            Icon(
                                imageVector = if(hiddenPassword)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if(hiddenPassword)
                        PasswordVisualTransformation()
                    else
                        VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    errorText = Res.string.wrong_password,
                    isError = viewModel.newPasswordError,
                    validator = { isPasswordValid(it) }
                )
            },
            confirmAction = {
                viewModel.changePassword(
                    onSuccess = resetPasswordLayout
                )
            },
            dismissText = Res.string.dismiss,
            confirmText = Res.string.confirm
        )
    }

    /**
     * Function to display the section of the user's language and allowing the user to change it
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun LanguageSection() {
        val changeLanguage = remember { mutableStateOf(false) }
        UserInfo(
            header = Res.string.language,
            info = activeLocalSession.language,
            editAction = { changeLanguage.value = true },

        )
        ChangeLanguage(
            changeLanguage = changeLanguage
        )
    }

    /**
     * Function to allow the user to change the current language setting
     *
     * @param changeLanguage: the state whether display this section
     */
    @Composable
    private fun ChangeLanguage(
        changeLanguage: MutableState<Boolean>
    ) {
        var selectedLanguage by remember { mutableStateOf(activeLocalSession.language) }
        EquinoxAlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
            show = changeLanguage,
            icon = Icons.Default.Language,
            title = Res.string.change_language,
            text = {
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
                viewModel.changeLanguage(
                    newLanguage = selectedLanguage,
                    onSuccess = {
                        changeLanguage.value = false
                        navToSplashscreen()
                    }
                )
            },
            dismissText = Res.string.dismiss,
            confirmText = Res.string.confirm
        )
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
                modifier = Modifier
                    .widthIn(
                        max = 400.dp
                    ),
                show = logout,
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = Res.string.logout,
                text = Res.string.account_logout_message,
                onDismissAction = { logout.value = false },
                confirmAction = {
                    viewModel.logout(
                        onLogout = { navToSplashscreen() }
                    )
                },
                dismissText = Res.string.dismiss,
                confirmText = Res.string.confirm
            )
            Spacer(modifier = Modifier.height(10.dp))
            ActionButton(
                color = md_theme_light_primary,
                action = { deleteAccount.value = true },
                text = Res.string.delete_account
            )
            EquinoxAlertDialog(
                modifier = Modifier
                    .widthIn(
                        max = 400.dp
                    ),
                show = deleteAccount,
                icon = Icons.Default.Clear,
                title = Res.string.delete_account,
                text = Res.string.account_deletion_message,
                onDismissAction = { deleteAccount.value = false },
                confirmAction = {
                    viewModel.deleteAccount(
                        onDelete = {
                            deleteAccount.value = false
                            navToSplashscreen()
                        },
                        onFailure = {
                            deleteAccount.value = false
                        }
                    )
                },
                dismissText = Res.string.dismiss,
                confirmText = Res.string.confirm
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
     * Function to navigate to the [SplashScreen]
     *
     * No-any params required
     */
    private fun navToSplashscreen() {
        navigator.popBackStack()
        navigator.navigate(SPLASH_SCREEN)
    }

    /**
     * Function invoked when the [ShowContent] composable has been started
     *
     * No-any params required
     */
    override fun onStart() {
        viewModel.setActiveContext(this::class.java)
        mySessions.addAll(localSessionsHelper.sessions)
    }

    /**
     * Function invoked when the [ShowContent] composable has been stopped
     *
     * No-any params required
     */
    override fun onStop() {
        mySessions.clear()
    }

}