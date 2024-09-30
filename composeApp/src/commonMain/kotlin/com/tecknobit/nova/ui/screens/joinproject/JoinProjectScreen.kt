@file:OptIn(ExperimentalMaterial3Api::class)

package com.tecknobit.nova.ui.screens.joinproject

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinoxcompose.components.EquinoxOutlinedTextField
import com.tecknobit.nova.fontFamily
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.ui.components.SplitText
import com.tecknobit.nova.ui.components.SplitTextState
import com.tecknobit.nova.ui.components.rememberSplitTextState
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.novacore.NovaInputValidator.isHostValid
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.host
import nova.composeapp.generated.resources.insert_join_code
import nova.composeapp.generated.resources.join
import nova.composeapp.generated.resources.join_project
import nova.composeapp.generated.resources.join_with_code
import nova.composeapp.generated.resources.wrong_host_address
import org.jetbrains.compose.resources.stringResource
import qrscanner.QrCodeScanner

class JoinProjectScreen(
    val enableScanOption: Boolean
) : NovaScreen() {

    companion object {

        private val viewModel = JoinProjectScreenViewModel(
            snackbarHostState = snackbarHostState
        )

    }

    private lateinit var splitTextState: SplitTextState

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
                if (!enableScanOption) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.White
                        ),
                        navigationIcon = { NavBackButton() },
                        title = {
                            Text(
                                text = stringResource(Res.string.join_project)
                            )
                        }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (enableScanOption) {
                    ScanOptionSection(
                        qrCodeScannerModifier = Modifier
                            .weight(1f)
                    )
                }
                JoinWithCodeSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            all = 16.dp
                        )
                        .then(
                            if (enableScanOption)
                                Modifier.weight(1f)
                            else {
                                Modifier
                                    .padding(
                                        top = it.calculateTopPadding() + 16.dp
                                    )
                            }
                        )
                )
            }
        }
    }

    @Composable
    @NonRestartableComposable
    private fun ScanOptionSection(
        qrCodeScannerModifier: Modifier
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            NavBackButton(
                tint = primaryColor
            )
        }
        JoinQRCodeScanner(
            modifier = qrCodeScannerModifier
        )
        TextDivider(
            text = stringResource(Res.string.join_with_code),
            textStyle = TextStyle(
                fontSize = 22.sp,
                fontFamily = fontFamily,
                color = primaryColor
            ),
            dividerColor = primaryColor,
            thickness = 2.dp
        )
    }

    @Composable
    @NonRestartableComposable
    private fun JoinQRCodeScanner(
        modifier: Modifier
    ) {
        AnimatedVisibility(
            visible = splitTextState.getCompleteText().isEmpty() && viewModel.host.value.isEmpty()
        ) {
            Column(
                modifier = modifier
            ) {
                QrCodeScanner(
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            bottom = 32.dp
                        )
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .size(250.dp),
                    flashlightOn = false,
                    onCompletion = { content ->
                        viewModel.joinWithScannedQR(
                            content = content
                        )
                    }
                )
            }
        }
    }

    @Composable
    @NonRestartableComposable
    private fun JoinWithCodeSection(
        modifier: Modifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                SplitText(
                    splitsTextState = splitTextState,
                    infoText = stringResource(Res.string.insert_join_code)
                )
                EquinoxOutlinedTextField(
                    modifier = Modifier
                        .padding(
                            top = 16.dp
                        ),
                    width = 350.dp,
                    value = viewModel.host,
                    label = Res.string.host,
                    isError = viewModel.hostError,
                    validator = { isHostValid(it) },
                    errorText = Res.string.wrong_host_address,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                TextButton(
                    onClick = {
                        viewModel.joinWithCode(
                            joinCode = splitTextState.getCompleteText()
                        )
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.join),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    @Deprecated(
        message = "Will be used the real one"
    )
    /**
     *
     * Component to divide sections by a representative text
     *
     * @param containerModifier: the [Modifier] to apply to the container row
     * @param fillMaxWidth: whether the composable must occupy the entire horizontal space
     * @param thickness: thickness of this divider line. Using [Dp.Hairline] will produce a single pixel
     * divider regardless of screen density
     * @param dividerColor: color of this divider line
     * @param text: the text message
     * @param textStyle: the style to apply to the [text]
     */
    @Composable
    @NonRestartableComposable
    private fun TextDivider(
        containerModifier: Modifier = Modifier,
        fillMaxWidth: Boolean = true,
        thickness: Dp = DividerDefaults.Thickness,
        dividerColor: Color = DividerDefaults.color,
        text: String,
        textStyle: TextStyle = TextStyle.Default
    ) {
        Row(
            modifier = containerModifier
                .then(
                    if (fillMaxWidth)
                        Modifier.fillMaxWidth()
                    else
                        Modifier
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CustomDivider(
                modifier = Modifier
                    .weight(1f),
                thickness = thickness,
                color = dividerColor
            )
            Text(
                text = text,
                style = textStyle,
                textAlign = TextAlign.Center
            )
            CustomDivider(
                modifier = Modifier
                    .weight(1f),
                thickness = thickness,
                color = dividerColor
            )
        }
    }

    @Deprecated(
        message = "Will be used the real one"
    )
    /**
     *
     * Custom divider to center the text message between two lines
     *
     * @param modifier: the [Modifier] to be applied to this divider line.
     * @param thickness: thickness of this divider line. Using [Dp.Hairline] will produce a single pixel
     * divider regardless of screen density.
     * @param color: color of this divider line.
     */
    @Composable
    @NonRestartableComposable
    private fun CustomDivider(
        modifier: Modifier = Modifier,
        thickness: Dp = DividerDefaults.Thickness,
        color: Color = DividerDefaults.color,
    ) {
        Canvas(
            modifier = modifier
        ) {
            drawLine(
                color = color,
                strokeWidth = thickness.toPx(),
                start = Offset(0f, thickness.toPx() / 2),
                end = Offset(size.width, thickness.toPx() / 2),
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(this::class.java)
    }

    @Composable
    override fun CollectStates() {
        super.CollectStates()
        viewModel.host = remember { mutableStateOf("") }
        viewModel.hostError = remember { mutableStateOf(false) }
        splitTextState = rememberSplitTextState(
            splits = 6
        )
    }

}