package com.tecknobit.nova.ui.screens.workonproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.equinoxcompose.components.EquinoxTextField
import com.tecknobit.nova.imageLoader
import com.tecknobit.nova.ui.components.MemberListItem
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.profile.getProfilePicPath
import com.tecknobit.novacore.NovaInputValidator.isProjectNameValid
import com.tecknobit.novacore.records.NovaUser
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.add_project
import nova.composeapp.generated.resources.edit_project
import nova.composeapp.generated.resources.logo
import nova.composeapp.generated.resources.members
import nova.composeapp.generated.resources.title
import nova.composeapp.generated.resources.wrong_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

abstract class WorkOnProject(
    projectId: String?
) : NovaScreen() {

    companion object {

        @JvmStatic
        protected val viewModel = WorkOnProjectViewModel(
            snackbarHostState = snackbarHostState
        )

    }

    protected lateinit var potentialMembers: State<List<NovaUser>>

    protected val isInAddMode = projectId == null

    @Composable
    override fun ArrangeScreenContent() {
        CollectStates()
    }

    @Composable
    @NonRestartableComposable
    protected fun ScreenTitle() {
        Text(
            text = stringResource(
                if (isInAddMode)
                    Res.string.add_project
                else
                    Res.string.edit_project
            )
        )
    }

    @Composable
    @NonRestartableComposable
    protected fun ProjectForm(
        paddingValues: PaddingValues
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                )
        ) {
            LogoSelector(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
            )
            ProjectTitleField()
            ProjectMembersSection()
        }
    }

    @Composable
    @NonRestartableComposable
    protected fun LogoSelector(
        modifier: Modifier
    ) {
        // TODO: TO SET THE REAL LOGO VALUE WHEN EDIT
        viewModel.logoPic = remember { mutableStateOf("") }
        viewModel.logoPicBordersColor = remember { mutableStateOf(Color.Transparent) }
        val launcher = rememberFilePickerLauncher(
            type = PickerType.Image,
            mode = PickerMode.Single,
        ) { picture ->
            val logoPath = getProfilePicPath(
                picture = picture
            )
            logoPath?.let {
                viewModel.logoPic.value = logoPath
                viewModel.logoPicBordersColor.value = Color.Transparent
            }
        }
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .shadow(
                        elevation = 3.dp,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .size(150.dp)
                    .border(
                        color = viewModel.logoPicBordersColor.value,
                        shape = CircleShape,
                        width = 1.dp
                    ),
                imageLoader = imageLoader,
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(viewModel.logoPic.value)
                    .crossfade(true)
                    .crossfade(500)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = painterResource(Res.drawable.logo)
            )
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xD0DFD8D8))
                    .align(Alignment.BottomEnd),
                onClick = { launcher.launch() }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    null
                )
            }
        }
    }

    @Composable
    @NonRestartableComposable
    protected fun ProjectTitleField() {
        viewModel.projectTitle = remember {
            mutableStateOf(
                if (isInAddMode)
                    ""
                else
                    "TODO INSERT THE NAME OF THE PROJECT"
            )
        }
        viewModel.projectTitleError = remember { mutableStateOf(false) }
        SectionHeader(
            header = Res.string.title
        )
        EquinoxTextField(
            modifier = Modifier
                .fillMaxWidth(),
            textFieldColors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent
            ),
            value = viewModel.projectTitle,
            isError = viewModel.projectTitleError,
            errorText = Res.string.wrong_title,
            label = null as StringResource?,
            validator = { isProjectNameValid(it) }
        )
    }

    @Composable
    @NonRestartableComposable
    protected fun ProjectMembersSection() {
        SectionHeader(
            header = Res.string.members
        )
        ProjectMembersSectionsImpl()
    }

    @Composable
    @NonRestartableComposable
    protected fun PotentialMembers(
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ) {
            items(
                items = potentialMembers.value,
                key = { member -> member.id }
            ) { member ->
                MemberListItem(
                    member = member,
                    trailingContent = {
                        var added by remember {
                            mutableStateOf(viewModel.membersAdded.contains(member.id))
                        }
                        Checkbox(
                            checked = added,
                            onCheckedChange = {
                                added = it
                                if (!added)
                                    viewModel.membersAdded.remove(member.id)
                                else
                                    viewModel.membersAdded.add(member.id)
                            }
                        )
                    }
                )
            }
        }
    }

    @Composable
    @NonRestartableComposable
    protected abstract fun ProjectMembersSectionsImpl()

    @Composable
    @NonRestartableComposable
    private fun SectionHeader(
        header: StringResource
    ) {
        Spacer(Modifier.height(15.dp))
        Text(
            text = stringResource(header),
            fontSize = 20.sp
        )
    }

    @Composable
    override fun CollectStates() {
        super.CollectStates()
        potentialMembers = viewModel.potentialMembers.collectAsState()
        viewModel.membersAdded = mutableStateListOf()
    }

    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(WorkOnProject::class.java)
    }

    /**
     * Function invoked when the [ShowContent] composable has been started
     *
     * No-any params required
     */
    override fun onStart() {
        super.onStart()
        viewModel.getPotentialMembers()
    }

    /**
     * Function invoked when the [ShowContent] composable has been resumed
     *
     * No-any params required
     */
    override fun onResume() {
        super.onResume()
        viewModel.restartRefresher()
    }

    /**
     * Function invoked when the [ShowContent] composable has been paused
     *
     * No-any params required
     */
    override fun onPause() {
        super.onPause()
        viewModel.suspendRefresher()
    }

    /**
     * Function invoked when the [ShowContent] composable has been stopped
     *
     * No-any params required
     */
    override fun onDestroy() {
        super.onDestroy()
        viewModel.suspendRefresher()
    }

}