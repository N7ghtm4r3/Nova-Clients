package com.tecknobit.nova.ui.screens.release

import com.tecknobit.apimanager.annotations.Wrapper
import io.github.vinceglb.filekit.core.PlatformFile

const val REPORTS_FOLDER = "reports/"

expect fun getAsset(
    asset: PlatformFile?
): String?

@Wrapper
expect fun downloadReport(
    report: String
)

expect fun downloadAssets(
    containerDirectoryPath: String,
    assets: List<String>
)