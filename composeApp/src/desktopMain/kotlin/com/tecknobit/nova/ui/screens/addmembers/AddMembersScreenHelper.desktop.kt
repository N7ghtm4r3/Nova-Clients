package com.tecknobit.nova.ui.screens.addmembers

import com.tecknobit.novacore.records.project.JoiningQRCode
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Function to share properly from each platform the details of the [joiningQRCode]
 *
 * @param joiningQRCode: the joining qrcode to share
 */
actual fun shareJoiningCode(
    joiningQRCode: JoiningQRCode
) {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(
        StringSelection(
            shareData(
                joiningQRCode = joiningQRCode
            )
        ),
        null
    )
}