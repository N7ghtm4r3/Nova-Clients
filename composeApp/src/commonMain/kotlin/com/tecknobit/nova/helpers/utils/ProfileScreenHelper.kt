package com.tecknobit.nova.helpers.utils

import io.github.vinceglb.filekit.core.PlatformFile

expect fun navToProfile()

expect fun getProfilePicPath(
    picture: PlatformFile?
): String?