package com.tecknobit.nova.ui.screens.release

import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.nova.ui.components.getAssetUrl
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent
import io.github.vinceglb.filekit.core.PlatformFile
import java.awt.Desktop
import java.io.File
import javax.swing.filechooser.FileSystemView

private val NOVA_MAIN_DIRECTORY = "${FileSystemView.getFileSystemView().homeDirectory}/Nova"

private val REPORTS_DIRECTORY = "$NOVA_MAIN_DIRECTORY/reports"

private val ASSETS_DIRECTORY = "$NOVA_MAIN_DIRECTORY/assets/"

actual fun getAsset(
    asset: PlatformFile?
): String? {
    return asset?.path
}

@Wrapper
actual fun downloadAssetsUploaded(
    assetsUploaded: List<AssetUploadingEvent.AssetUploaded>
) {
    downloadAssets(
        containerDirectoryPath = ASSETS_DIRECTORY,
        getAssetName = { index -> assetsUploaded[index].name },
        assets = assetsUploaded.map { asset -> getAssetUrl(asset.url) }
    )
}

@Wrapper
actual fun downloadReport(
    report: String
) {
    downloadAssets(
        containerDirectoryPath = REPORTS_DIRECTORY,
        getAssetName = { report.substringAfterLast("/") },
        assets = listOf(report)
    )
}

actual fun downloadAssets(
    containerDirectoryPath: String,
    getAssetName: (Int) -> String,
    assets: List<String>
) {
    val containerDirectory = File(containerDirectoryPath)
    if (!containerDirectory.exists())
        containerDirectory.mkdirs()
    performAssetsDownload(
        containerDirectoryPath = containerDirectoryPath,
        assets = assets,
        getAssetName = getAssetName
    )
}

actual fun openAsset(
    asset: File
) {
    Desktop.getDesktop().open(asset)
}