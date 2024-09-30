package com.tecknobit.nova.ui.screens.profile

import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROFILE_DIALOG

actual fun navToProfile() {
    navigator.navigate(PROFILE_DIALOG)
}