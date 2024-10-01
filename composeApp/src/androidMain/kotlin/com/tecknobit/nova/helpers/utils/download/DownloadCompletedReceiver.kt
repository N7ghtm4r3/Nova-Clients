package com.tecknobit.nova.helpers.utils.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tecknobit.nova.helpers.utils.download.AssetDownloader.Companion.lastDownloadToWait

/**
 * The `DownloadCompletedReceiver` class is useful to receive the
 * [DownloadManager.ACTION_DOWNLOAD_COMPLETE] action and then open the file manager of the device
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see BroadcastReceiver
 */
class DownloadCompletedReceiver : BroadcastReceiver() {

    /**
     * {@inheritDoc}
     */
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val action = intent.action
        if (action != null && action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == lastDownloadToWait) {
                val fileManager = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
                fileManager.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fileManager)
            }
        }
    }

}
