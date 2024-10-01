package com.tecknobit.nova.helpers.utils.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.core.net.toUri

/**
 * The {@code AssetDownloader} class is useful to manage the downloads on the device of the assets
 * uploaded
 *
 * @param context: the context where the [AssetDownloader] has been invoked
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class AssetDownloader (
    context: Context
) {

    companion object {

        /**
         * ***lastDownloadToWait* the timestamp of the last download to wait before open the archive manager
         * of the device
         */
        var lastDownloadToWait = -1L

    }

    /**
     * ***downloader* the system downloader used to enqueue the downloads requests
     */
    private val downloader = context.getSystemService(DownloadManager::class.java)

    /**
     * Function to download the asset
     *
     * @param url: the url to reach and install the asset
     */
    fun downloadAsset(
        url: String
    ) {
        val assetName = url.substringAfterLast("/")
        val request = DownloadManager.Request(url.toUri())
            .setTitle(assetName)
            .setDestinationInExternalPublicDir(
                DIRECTORY_DOWNLOADS,
                assetName
            )
        lastDownloadToWait = downloader.enqueue(request)
    }

}