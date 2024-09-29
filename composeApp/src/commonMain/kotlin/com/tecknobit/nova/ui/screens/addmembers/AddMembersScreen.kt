@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.tecknobit.nova.ui.screens.addmembers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinoxcompose.components.EquinoxOutlinedTextField
import com.tecknobit.nova.customerColor
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.vendorColor
import com.tecknobit.novacore.NovaInputValidator.isEmailValid
import com.tecknobit.novacore.records.NovaUser.Role
import com.tecknobit.novacore.records.project.Project
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.add_members
import nova.composeapp.generated.resources.email
import nova.composeapp.generated.resources.wrong_email
import org.jetbrains.compose.resources.stringResource

class AddMembersScreen(
    val project: Project
) : NovaScreen() {

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
                    onClick = { viewModel.addMembers() }
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null
                    )
                }
            }
        ) { paddingValues ->
            PotentialMembers(
                paddingValues = paddingValues
            )
        }
    }

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
                    onClick = {
                        viewModel.members.add(Pair("", Role.Customer))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
            itemsIndexed(
                items = viewModel.members,
            ) { index, _ ->
                MemberForm(
                    index = index
                )
            }
        }
    }

    @Composable
    @NonRestartableComposable
    private fun MemberForm(
        index: Int
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val email = remember { mutableStateOf("") }
            val role = remember { mutableStateOf(Role.Customer) }
            var fieldHeight by remember { mutableStateOf(55.dp) }
            val localDensity = LocalDensity.current
            EquinoxOutlinedTextField(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        fieldHeight = with(localDensity) { coordinates.size.height.toDp() }
                    },
                width = 220.dp,
                mustBeInLowerCase = true,
                value = email,
                label = Res.string.email,
                validator = { isEmailValid(email.value) },
                errorText = Res.string.wrong_email
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
                    .clickable { viewModel.members.removeAt(index) }
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

    override fun onStart() {
        super.onStart()
        viewModel.setActiveContext(this::class.java)
        viewModel.members.add(Pair("", Role.Customer))
    }

}