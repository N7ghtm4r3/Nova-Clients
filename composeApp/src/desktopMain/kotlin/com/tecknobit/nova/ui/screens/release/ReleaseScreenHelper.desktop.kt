package com.tecknobit.nova.ui.screens.release

import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.nova.ui.components.getAssetUrl
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent
import io.github.vinceglb.filekit.core.PlatformFile
import java.awt.Desktop
import java.io.File
import javax.swing.filechooser.FileSystemView

/**
 * **NOVA_MAIN_DIRECTORY** -> the main directory where store the file downloaded
 */
private val NOVA_MAIN_DIRECTORY = "${FileSystemView.getFileSystemView().homeDirectory}/Nova"

/**
 * **REPORTS_DIRECTORY** -> the directory where save the reports file downloaded
 */
private val REPORTS_DIRECTORY = "$NOVA_MAIN_DIRECTORY/reports"

/**
 * **ASSETS_DIRECTORY** -> the directory where save the assets file downloaded
 */
private val ASSETS_DIRECTORY = "$NOVA_MAIN_DIRECTORY/assets/"

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
    return asset?.path
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
        containerDirectoryPath = ASSETS_DIRECTORY,
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
        containerDirectoryPath = REPORTS_DIRECTORY,
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
    val containerDirectory = File(containerDirectoryPath)
    if (!containerDirectory.exists())
        containerDirectory.mkdirs()
    performAssetsDownload(
        containerDirectoryPath = containerDirectoryPath,
        assets = assets,
        getAssetName = getAssetName
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
    Desktop.getDesktop().open(asset)
}