package com.tecknobit.nova.ui.screens.addmembers

import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.novacore.records.project.JoiningQRCode

/**
 * Function to share properly from each platform the details of the [joiningQRCode]
 *
 * @param joiningQRCode: the joining qrcode to share
 */
expect fun shareJoiningCode(
    joiningQRCode: JoiningQRCode
)

/**
 * Function to share the data of the [joiningQRCode]
 *
 * @param joiningQRCode: the joining qrcode to share
 */
fun shareData(
    joiningQRCode: JoiningQRCode
): String {
    return "Join code: ${joiningQRCode.joinCode}\nLink: ${
        joiningQRCode.getShareLink(
            activeLocalSession.hostAddress
        )
    }"
}