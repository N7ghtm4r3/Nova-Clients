package com.tecknobit.nova.ui.screens.addmembers

import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.novacore.records.project.JoiningQRCode

expect fun shareJoiningCode(
    joiningQRCode: JoiningQRCode
)

fun shareData(
    joiningQRCode: JoiningQRCode
): String {
    return "Join code: ${joiningQRCode.joinCode}\nLink: ${
        joiningQRCode.getShareLink(
            activeLocalSession.hostAddress
        )
    }"
}