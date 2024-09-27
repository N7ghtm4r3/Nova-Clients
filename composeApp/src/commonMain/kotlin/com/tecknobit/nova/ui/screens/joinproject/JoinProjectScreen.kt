@file:OptIn(ExperimentalMaterial3Api::class)

package com.tecknobit.nova.ui.screens.joinproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.nova.ui.components.SplitText
import com.tecknobit.nova.ui.screens.NovaScreen
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (enableScanOption)
                JoinQRCodeScanner()
            JoinWithCodeSection()
        }
    }

    @Composable
    @NonRestartableComposable
    private fun JoinQRCodeScanner() {
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

    @Composable
    @NonRestartableComposable
    private fun JoinWithCodeSection() {
        Column(
            modifier = Modifier
                .padding(
                    all = 16.dp
                )
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Join with code",
                color = Color.White,
                fontSize = 24.sp
            )
            SplitText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                splits = 6
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        viewModel.setActiveContext(this::class.java)
    }

}