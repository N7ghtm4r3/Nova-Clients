package com.tecknobit.nova.ui.screens.profile

import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROFILE_DIALOG
import io.github.vinceglb.filekit.core.PlatformFile

actual fun navToProfile() {
    navigator.navigate(PROFILE_DIALOG)
}

actual fun getProfilePicPath(
    picture: PlatformFile?
): String? {
    return picture!!.path
}