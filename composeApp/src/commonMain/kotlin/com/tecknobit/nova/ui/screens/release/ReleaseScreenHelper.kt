package com.tecknobit.nova.ui.screens.release

import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded
import io.github.vinceglb.filekit.core.PlatformFile
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * **sslContext** -> the context helper to TLS protocols
 */
private val sslContext = SSLContext.getInstance("TLS")

/**
 * **HTTPS_PROTOCOL** -> the HTTPS protocol text
 */
private const val HTTPS_PROTOCOL = "https"

/**
 * Function to get the asset's path
 *
 * @param asset: the asset from fetch its path
 *
 * @return the asset path as [String]
 */
expect fun getAsset(
    asset: PlatformFile?
): String?

/**
 * Function to download the assets uploaded
 *
 * @param assetsUploaded: the list of the assets uploaded
 */
@Wrapper
expect fun downloadAssetsUploaded(
    assetsUploaded: List<AssetUploaded>
)

/**
 * Function to download a [Release]'s report
 *
 * @param report: the report url
 */
@Wrapper
expect fun downloadReport(
    report: String
)

/**
 * Wrapper function to download the assets uploaded
 *
 * @param containerDirectoryPath: the directory container where save the assets downloaded
 * @param getAssetName: function to invoke to get the name of the asset
 * @param assets: the list of the assets uploaded
 */
expect fun downloadAssets(
    containerDirectoryPath: String,
    getAssetName: (Int) -> String,
    assets: List<String>
)

/**
 * Function to perform the assets download
 *
 * @param containerDirectoryPath: the directory container where save the assets downloaded
 * @param getAssetName: function to invoke to get the name of the asset
 * @param assets: the list of the assets uploaded
 */
fun performAssetsDownload(
    containerDirectoryPath: String,
    getAssetName: (Int) -> String,
    assets: List<String>
) {
    val lastAsset = assets.last()
    assets.forEachIndexed { index, assetUrl ->
        val client: OkHttpClient = if (assetUrl.startsWith(HTTPS_PROTOCOL)) {
            sslContext.init(null, validateSelfSignedCertificate(), SecureRandom())
            OkHttpClient().newBuilder()
                .sslSocketFactory(
                    sslContext.socketFactory,
                    validateSelfSignedCertificate()[0] as X509TrustManager
                )
                .hostnameVerifier { _, _ -> true }
                .build()
        } else
            OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url(assetUrl)
            .build()
        val response = client.newCall(request).execute()
        val asset = File(containerDirectoryPath, getAssetName(index))
        response.body?.byteStream()?.use { inputStream ->
            try {
                FileOutputStream(asset).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } catch (_: FileNotFoundException) {
            }
        }
        if (lastAsset == assetUrl) {
            openAsset(
                asset = asset
            )
        }
    }
}

/**
 * Function to open an asset downloaded
 *
 * @param asset: the asset to open
 */
expect fun openAsset(
    asset: File
)

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