package com.tecknobit.nova.ui.screens.addmembers

import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.tecknobit.nova.helpers.utils.AppContext
import com.tecknobit.novacore.records.project.JoiningQRCode

/**
 * Function to share properly from each platform the details of the [joiningQRCode]
 *
 * @param joiningQRCode: the joining qrcode to share
 */
actual fun shareJoiningCode(
    joiningQRCode: JoiningQRCode
) {
    val intent = Intent()
    intent.type = "text/plain"
    intent.action = Intent.ACTION_SEND
    intent.putExtra(
        EXTRA_TEXT,
        shareData(joiningQRCode = joiningQRCode)
    )
    AppContext.get().startActivity(
        Intent.createChooser(intent, null).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
    )
}