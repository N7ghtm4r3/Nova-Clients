package com.tecknobit.nova.ui.screens.release

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.OpenableColumns
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.nova.helpers.utils.AppContext
import com.tecknobit.nova.ui.components.getAssetUrl
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent
import io.github.vinceglb.filekit.core.PlatformFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.min

/**
 * Function to get the asset's path
 *
 * @param asset: the asset from fetch its path
 *
 * @return the asset path as [String]
 */
actual fun getAsset(
    asset: PlatformFile?
): String? {
    if (asset != null) {
        return getFilePath(
            context = AppContext.get(),
            uri = asset.uri
        )
    }
    return null
}

/**
 * Function to get the complete file path of an file
 *
 * @param context: the context where the file is needed
 * @param uri: the uri of the file
 * @return the path of the file as [String]
 */
private fun getFilePath(
    context: Context,
    uri: Uri
): String {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.getLong(sizeIndex).toString()
    val file = File(context.filesDir, name)
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable: Int = inputStream?.available() ?: 0
        val bufferSize = min(bytesAvailable, maxBufferSize)
        val buffers = ByteArray(bufferSize)
        while (inputStream?.read(buffers).also {
                if (it != null) {
                    read = it
                }
            } != -1) {
            outputStream.write(buffers, 0, read)
        }
        inputStream?.close()
        outputStream.close()
    } catch (_: Exception) {
    } finally {
        returnCursor.close()
    }
    return file.path
}

/**
 * Function to download the assets uploaded
 *
 * @param assetsUploaded: the list of the assets uploaded
 */
@Wrapper
actual fun downloadAssetsUploaded(
    assetsUploaded: List<AssetUploadingEvent.AssetUploaded>
) {
    downloadAssets(
        containerDirectoryPath = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).path,
        getAssetName = { index -> assetsUploaded[index].name },
        assets = assetsUploaded.map { asset -> getAssetUrl(asset.url) }
    )
}

/**
 * Function to download a [Release]'s report
 *
 * @param report: the report url
 */
@Wrapper
actual fun downloadReport(
    report: String
) {
    downloadAssets(
        containerDirectoryPath = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).path,
        getAssetName = { report.substringAfterLast("/") },
        assets = listOf(report)
    )
}

/**
 * Wrapper function to download the assets uploaded
 *
 * @param containerDirectoryPath: the directory container where save the assets downloaded
 * @param getAssetName: function to invoke to get the name of the asset
 * @param assets: the list of the assets uploaded
 */
actual fun downloadAssets(
    containerDirectoryPath: String,
    getAssetName: (Int) -> String,
    assets: List<String>
) {
    performAssetsDownload(
        containerDirectoryPath = containerDirectoryPath,
        getAssetName = getAssetName,
        assets = assets
    )
}

/**
 * Function to open an asset downloaded
 *
 * @param asset: the asset to open
 */
actual fun openAsset(
    asset: File
) {
    val fileManager = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
    fileManager.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    AppContext.get().startActivity(fileManager)
}