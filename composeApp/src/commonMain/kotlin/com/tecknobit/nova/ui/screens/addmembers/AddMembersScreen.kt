@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.tecknobit.nova.ui.screens.addmembers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinoxcompose.components.EquinoxOutlinedTextField
import com.tecknobit.equinoxcompose.components.ErrorUI
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.components.customerColor
import com.tecknobit.nova.ui.components.vendorColor
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.theme.gray_background
import com.tecknobit.novacore.NovaInputValidator.isEmailValid
import com.tecknobit.novacore.records.NovaUser.Role
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.add_members
import nova.composeapp.generated.resources.email
import nova.composeapp.generated.resources.failed_to_create_qrcode
import nova.composeapp.generated.resources.retry
import nova.composeapp.generated.resources.wrong_email
import org.jetbrains.compose.resources.stringResource
import qrgenerator.qrkitpainter.rememberQrKitPainter

/**
 * The [AddMembersScreen] class is used to add new members to the [project]
 *
 * @param project: the project where the user have to be added
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxScreen
 * @see NovaScreen
 */
class AddMembersScreen(
    val project: Project
) : NovaScreen() {

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = AddMembersScreenViewModel(
        snackbarHostState = snackbarHostState,
        project = project
    )

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        CollectStates()
        Scaffold(
            containerColor = gray_background,
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = { NavBackButton() },
                    title = {
                        Column {
                            Text(
                                text = project.name,
                                fontSize = 16.sp
                            )
                            Text(
                                text = stringResource(Res.string.add_members)
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (viewModel.qrcodeCreationAccepted.value)
                            navigator.goBack()
                        else
                            viewModel.addMembers()
                    }
                ) {
                    Icon(
                        imageVector = if (viewModel.qrcodeCreationAccepted.value)
                            Icons.Default.DoneAll
                        else
                            Icons.Default.Done,
                        contentDescription = null
                    )
                }
            }
        ) { paddingValues ->
            AnimatedVisibility(
                visible = !viewModel.qrcodeCreationAccepted.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PotentialMembers(
                    paddingValues = paddingValues
                )
            }
            AnimatedVisibility(
                visible = viewModel.qrcodeCreationAccepted.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                QRCodeResult(
                    paddingValues = paddingValues
                )
            }
        }
    }

    /**
     * Section where the user can add the details of the new members to add
     *
     * @param paddingValues: the padding values to apply to the section
     */
    @Composable
    @NonRestartableComposable
    private fun PotentialMembers(
        paddingValues: PaddingValues
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                )
        ) {
            stickyHeader {
                FloatingActionButton(
                    modifier = Modifier
                        .size(40.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { viewModel.addEmptyItem() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
            itemsIndexed(
                items = viewModel.members
            ) { index, _ ->
                MemberForm(
                    index = index
                )
            }
        }
    }

    /**
     * Form for the insertion of the details about the new member to add
     *
     * @param index: the position in the members list, is useful for the remove
     */
    @Composable
    @NonRestartableComposable
    private fun MemberForm(
        index: Int
    ) {
        val member = viewModel.members[index]
        val email = mutableStateOf(member.first)
        val emailError = remember { mutableStateOf(false) }
        val role = remember { mutableStateOf(member.second) }
        var fieldHeight by remember { mutableStateOf(55.dp) }
        val localDensity = LocalDensity.current
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EquinoxOutlinedTextField(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        fieldHeight = with(localDensity) { coordinates.size.height.toDp() }
                    },
                width = 220.dp,
                mustBeInLowerCase = true,
                value = email,
                isError = emailError,
                onValueChange = {
                    email.value = it
                    emailError.value = !isEmailValid(email.value)
                    viewModel.addMember(
                        index = index,
                        email = it,
                        role = role.value
                    )
                },
                label = Res.string.email,
                validator = { isEmailValid(email.value) },
                errorText = Res.string.wrong_email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            RoleSelector(
                modifier = Modifier
                    .weight(1f)
                    .height(fieldHeight),
                index = index,
                role = role
            )
        }
    }

    /**
     * Selector for the role of the member
     *
     * @param modifier: the modifier to apply to the selector
     * @param index: the position in the members list, is useful for the remove
     * @param role: the state to manage the role to use for the member
     */
    @Composable
    @NonRestartableComposable
    private fun RoleSelector(
        modifier: Modifier,
        index: Int,
        role: MutableState<Role>
    ) {
        val selectRole = remember { mutableStateOf(false) }
        Column(
            modifier = modifier
                .padding(
                    top = 5.dp
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 5.dp,
                            topEnd = 5.dp
                        )
                    )
                    .background(
                        if (role.value == Role.Vendor)
                            vendorColor
                        else
                            customerColor
                    )
                    .clickable { selectRole.value = true }
                    .weight(.8f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = role.value.name,
                    color = Color.White
                )
            }
            RolesMenu(
                selectRole = selectRole,
                role = role
            )
            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 5.dp,
                            bottomEnd = 5.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { viewModel.removeMember(index) }
                    .weight(.8f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    /**
     * Menu to select the role for the member
     *
     * @param selectRole: whether the menu is visible
     * @param role: the state to manage the role to use for the member
     */
    @Composable
    @NonRestartableComposable
    private fun RolesMenu(
        selectRole: MutableState<Boolean>,
        role: MutableState<Role>
    ) {
        DropdownMenu(
            expanded = selectRole.value,
            onDismissRequest = { selectRole.value = false }
        ) {
            Role.entries.forEach { roleOption ->
                if (roleOption != Role.Tester) {
                    DropdownMenuItem(
                        onClick = {
                            role.value = roleOption
                            selectRole.value = false
                        },
                        text = {
                            Text(
                                text = roleOption.name
                            )
                        }
                    )
                }
            }
        }
    }

    /**
     * Section where is displayed the result about the qrcode creation
     *
     * @param paddingValues: the padding values to apply to the section
     */
    @Composable
    @NonRestartableComposable
    private fun QRCodeResult(
        paddingValues: PaddingValues
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Loading()
            QRCodeData(
                paddingValues = paddingValues
            )
            StatusFailed()
        }
    }

    /**
     * Section displayed when the qrcode has been generating
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun Loading() {
        AnimatedVisibility(
            visible = viewModel.creatingQRCode.value && !viewModel.errorDuringCreation.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator()
        }
    }

    /**
     * Section where are displayed the data of the qrcode generated
     *
     * @param paddingValues: the padding values to apply to the section
     */
    @Composable
    @NonRestartableComposable
    private fun QRCodeData(
        paddingValues: PaddingValues
    ) {
        AnimatedVisibility(
            visible = !viewModel.creatingQRCode.value && !viewModel.errorDuringCreation.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(250.dp),
                    contentColor = gray_background,
                    shape = RoundedCornerShape(
                        5.dp
                    ),
                    shadowElevation = 3.dp
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = rememberQrKitPainter(
                            data = viewModel.getQRCodeData()
                        ),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = null
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.joiningQRCode.joinCode,
                        letterSpacing = 4.sp,
                        fontSize = 25.sp
                    )
                    IconButton(
                        onClick = {
                            shareJoiningCode(
                                joiningQRCode = viewModel.joiningQRCode
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    /**
     * Section displayed when the qrcode generation failed
     *
     * No-any params required
     */
    @Composable
    @NonRestartableComposable
    private fun StatusFailed() {
        AnimatedVisibility(
            visible = viewModel.errorDuringCreation.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ErrorUI(
                errorIcon = Icons.Default.Error,
                errorMessage = Res.string.failed_to_create_qrcode,
                retryText = Res.string.retry,
                retryAction = {
                    viewModel.errorDuringCreation.value = false
                    viewModel.creatingQRCode.value = true
                    viewModel.qrcodeCreationAccepted.value = false
                }
            )
        }
    }

    /**
     * Function invoked when the [ShowContent] composable has been created
     *
     * No-any params required
     */
    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(this::class.java)
    }

    /**
     * Function invoked when the [ShowContent] composable has been started
     *
     * No-any params required
     */
    override fun onStart() {
        super.onStart()
        viewModel.addEmptyItem()
    }

    /**
     * Function to collect or instantiate the states of the screen
     *
     * No-any params required
     */
    @Composable
    override fun CollectStates() {
        super.CollectStates()
        viewModel.members = remember { mutableStateListOf() }
        viewModel.qrcodeCreationAccepted = remember { mutableStateOf(false) }
        viewModel.creatingQRCode = remember { mutableStateOf(false) }
        viewModel.errorDuringCreation = remember { mutableStateOf(false) }
    }

}