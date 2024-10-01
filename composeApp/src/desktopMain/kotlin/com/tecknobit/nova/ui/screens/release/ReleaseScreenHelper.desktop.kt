package com.tecknobit.nova.ui.screens.release

import com.tecknobit.apimanager.annotations.Wrapper
import io.github.vinceglb.filekit.core.PlatformFile
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.swing.filechooser.FileSystemView

private val NOVA_MAIN_DIRECTORY = "${FileSystemView.getFileSystemView().homeDirectory}/Nova"

private val REPORTS_DIRECTORY = "$NOVA_MAIN_DIRECTORY/$REPORTS_FOLDER"

actual fun getAsset(
    asset: PlatformFile?
): String? {
    return asset?.path
}

/**
 * **sslContext** -> the context helper to TLS protocols
 */
private val sslContext = SSLContext.getInstance("TLS")

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
    val lastAsset = assets.last()
    assets.forEach { assetUrl ->
        val website = URL(assetUrl)
        val connection = website.openConnection()
        if (connection is HttpsURLConnection) {
            sslContext.init(null, validateSelfSignedCertificate(), SecureRandom())
            connection.sslSocketFactory = sslContext.socketFactory
        }
        val assetPath = containerDirectoryPath + assetUrl.substringAfterLast("/")
        connection.inputStream.use { input ->
            FileOutputStream(File(assetPath)).use { output ->
                input.copyTo(output)
            }
        }
        if (lastAsset == assetUrl)
            Desktop.getDesktop().open(File(assetPath))
    }
}

/**
 * Method to validate a self-signed SLL certificate and bypass the checks of its validity<br></br>
 * No-any params required
 *
 * @return list of trust managers as [Array] of [TrustManager]
 * @apiNote this method disable all checks on the SLL certificate validity, so is recommended to
 * use for test only or in a private distribution on own infrastructure
 */
private fun validateSelfSignedCertificate(): Array<TrustManager> {
    return arrayOf(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
    })
}