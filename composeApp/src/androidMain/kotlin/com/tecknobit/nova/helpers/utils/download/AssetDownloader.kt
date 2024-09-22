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
         * ***NOVA_ASSETS_PATH* the path where store the asset downloaded
         */
        const val NOVA_ASSETS_PATH: String = "Nova/"

    }

    /**
     * ***downloader* the system downloader used to enqueue the downloads requests
     */
    private val downloader = context.getSystemService(DownloadManager::class.java)

    /**
     * ***lastDownloadToWait* the timestamp of the last download to wait before open the archive manager
     * of the device
     */
    var lastDownloadToWait = -1L

    /**
     * Function to download the asset
     *
     * @param url: the url to reach and install the asset
     */
    fun downloadAsset(url: String) {
        val assetName = url.split("/").last()
        val request = DownloadManager.Request(url.toUri())
            .setTitle(assetName)
            .setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, "$NOVA_ASSETS_PATH$assetName")
        lastDownloadToWait = downloader.enqueue(request)
    }

}