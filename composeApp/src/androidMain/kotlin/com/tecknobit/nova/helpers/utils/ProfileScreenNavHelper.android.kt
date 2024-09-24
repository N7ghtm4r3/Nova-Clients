package com.tecknobit.nova.helpers.utils

import com.tecknobit.nova.navigator
import com.tecknobit.nova.screens.NovaScreen.Companion.PROFILE_SCREEN

actual fun navToProfile() {
    navigator.navigate(PROFILE_SCREEN)
}