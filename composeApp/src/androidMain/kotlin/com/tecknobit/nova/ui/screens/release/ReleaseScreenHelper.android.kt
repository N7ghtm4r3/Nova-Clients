package com.tecknobit.nova.ui.screens.release

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.nova.helpers.utils.AppContext
import com.tecknobit.nova.helpers.utils.download.AssetDownloader
import io.github.vinceglb.filekit.core.PlatformFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.min

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

@Wrapper
actual fun downloadReport(
    report: String
) {
    downloadAssets(
        containerDirectoryPath = REPORTS_FOLDER,
        assets = listOf(report)
    )
}

actual fun downloadAssets(
    containerDirectoryPath: String,
    assets: List<String>
) {
    val downloader = AssetDownloader(AppContext.get())
    assets.forEach { assetUrl ->
        downloader.downloadAsset(
            url = assetUrl
        )
    }
}