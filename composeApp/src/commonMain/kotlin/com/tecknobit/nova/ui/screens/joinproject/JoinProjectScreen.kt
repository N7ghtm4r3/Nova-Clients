@file:OptIn(ExperimentalMaterial3Api::class)

package com.tecknobit.nova.ui.screens.joinproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.tecknobit.equinoxcompose.components.EquinoxTextField
import com.tecknobit.equinoxcompose.components.TextDivider
import com.tecknobit.nova.ui.components.SplitText
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.novacore.NovaInputValidator.isHostValid
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.host
import nova.composeapp.generated.resources.wrong_host_address
import qrscanner.QrCodeScanner

class JoinProjectScreen(
    val enableScanOption: Boolean
) : NovaScreen() {

    companion object {

        private val viewModel = JoinProjectScreenViewModel(
            snackbarHostState = snackbarHostState
        )

    }

    /**
     * Function to arrange the content of the screen to display
     *
     * No-any params required
     */
    @Composable
    override fun ArrangeScreenContent() {
        CollectStates()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (enableScanOption) {
                JoinQRCodeScanner(
                    modifier = Modifier
                        .weight(1f)
                )
            }
            TextDivider(
                text = "Join with code",
                fillMaxWidth = false
            )
            JoinWithCodeSection(
                modifier = Modifier
                    .weight(1f)
            )
        }
    }

    @Composable
    @NonRestartableComposable
    private fun JoinQRCodeScanner(
        modifier: Modifier
    ) {
        Column(
            modifier = modifier
        ) {
            QrCodeScanner(
                modifier = Modifier
                    .padding(
                        top = TopAppBarDefaults.TopAppBarExpandedHeight
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

    @Composable
    @NonRestartableComposable
    private fun JoinWithCodeSection(
        modifier: Modifier
    ) {
        Column(
            modifier = modifier
                .padding(
                    all = 16.dp
                )
        ) {
            EquinoxTextField(
                value = viewModel.host,
                label = Res.string.host,
                isError = viewModel.hostError,
                validator = { isHostValid(it) },
                errorText = Res.string.wrong_host_address
            )
            SplitText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                splits = 6
            )
        }
    }

    @Composable
    override fun CollectStates() {
        super.CollectStates()
        viewModel.host = remember { mutableStateOf("") }
        viewModel.hostError = remember { mutableStateOf(false) }
    }

    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(this::class.java)
    }

}