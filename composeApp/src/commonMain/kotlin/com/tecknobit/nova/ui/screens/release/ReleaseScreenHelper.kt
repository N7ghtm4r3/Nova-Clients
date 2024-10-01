package com.tecknobit.nova.ui.screens.release

import com.tecknobit.apimanager.annotations.Wrapper
import io.github.vinceglb.filekit.core.PlatformFile
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
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

private const val HTTPS_PROTOCOL = "https"

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

fun performAssetsDownload(
    containerDirectoryPath: String,
    assets: List<String>
) {
    val lastAsset = assets.last()
    assets.forEach { assetUrl ->
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
        val asset = File(containerDirectoryPath, assetUrl.substringAfterLast("/"))
        response.body?.byteStream()?.use { inputStream ->
            FileOutputStream(asset).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        if (lastAsset == assetUrl) {
            openAsset(
                asset = asset
            )
        }
    }
}

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