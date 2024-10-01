package com.tecknobit.nova.ui.screens.release

import com.tecknobit.apimanager.annotations.Wrapper
import io.github.vinceglb.filekit.core.PlatformFile
import java.awt.Desktop
import java.io.File
import javax.swing.filechooser.FileSystemView

private val NOVA_MAIN_DIRECTORY = "${FileSystemView.getFileSystemView().homeDirectory}/Nova"

private const val REPORTS_FOLDER = "reports/"

private val REPORTS_DIRECTORY = "$NOVA_MAIN_DIRECTORY/$REPORTS_FOLDER"

actual fun getAsset(
    asset: PlatformFile?
): String? {
    return asset?.path
}

@Wrapper
actual fun downloadReport(
    report: String
) {
    downloadAssets(
        containerDirectoryPath = REPORTS_DIRECTORY,
        assets = listOf(report)
    )
}

actual fun downloadAssets(
    containerDirectoryPath: String,
    assets: List<String>
) {
    val containerDirectory = File(containerDirectoryPath)
    if (!containerDirectory.exists())
        containerDirectory.mkdirs()
    performAssetsDownload(
        containerDirectoryPath = containerDirectoryPath,
        assets = assets
    )
}

actual fun openAsset(
    asset: File
) {
    Desktop.getDesktop().open(asset)
}