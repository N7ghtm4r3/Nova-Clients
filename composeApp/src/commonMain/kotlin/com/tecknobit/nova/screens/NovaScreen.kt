package com.tecknobit.nova.screens

import androidx.compose.runtime.Composable
import com.tecknobit.apimanager.annotations.Structure
import com.tecknobit.equinoxcompose.helpers.session.EquinoxScreen

@Structure
abstract class NovaScreen: EquinoxScreen() {

    companion object {

        const val SPLASH_SCREEN = "Splashscreen"

    }

    @Composable
    protected open fun CollectStates() {

    }

}