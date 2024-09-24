package com.tecknobit.nova.ui.screens.profile

import io.github.vinceglb.filekit.core.PlatformFile

expect fun navToProfile()

expect fun getProfilePicPath(
    picture: PlatformFile?
): String?