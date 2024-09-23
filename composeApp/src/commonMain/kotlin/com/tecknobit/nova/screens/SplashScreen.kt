package com.tecknobit.nova.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.nova.cache.LocalSessionHelper
import com.tecknobit.nova.helpers.utils.CheckForUpdatesAndLaunch
import com.tecknobit.nova.theme.md_theme_light_primary
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession
import com.tecknobit.novacore.helpers.NovaRequester
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

class SplashScreen: NovaScreen() {

    companion object {

        /**
         * **requester** -> the instance to manage the requests with the backend
         */
        lateinit var requester: NovaRequester

        /**
         * **localSessionsHelper** -> the helper to manage the local sessions stored locally in
         * the device
         */
        lateinit var localSessionsHelper: LocalSessionHelper

        /**
         * **activeLocalSession** -> the current active session that user is using
         */
        lateinit var activeLocalSession: NovaSession

    }

    @Composable
    override fun ArrangeScreenContent() {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(md_theme_light_primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(Res.string.app_name),
                    fontSize = 55.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            Row (
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        bottom = 20.dp
                    ),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "by Tecknobit",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            CheckForUpdatesAndLaunch()
        }
    }

}