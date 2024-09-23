package com.tecknobit.nova.helpers.utils

import android.content.Intent
import android.net.Uri

actual fun openLink(
    url: String
) {
    val context = AppContext.get()
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}