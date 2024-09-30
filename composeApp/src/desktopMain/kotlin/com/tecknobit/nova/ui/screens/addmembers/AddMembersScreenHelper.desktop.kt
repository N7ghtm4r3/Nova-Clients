package com.tecknobit.nova.ui.screens.addmembers

import com.tecknobit.novacore.records.project.JoiningQRCode
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

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