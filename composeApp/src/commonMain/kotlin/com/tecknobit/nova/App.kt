package com.tecknobit.nova

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import coil3.ImageLoader
import coil3.addLastModifiedToFileCacheKey
import coil3.compose.LocalPlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import com.tecknobit.nova.screens.NovaScreen.Companion.SPLASH_SCREEN
import com.tecknobit.nova.screens.SplashScreen
import com.tecknobit.nova.theme.NovaTheme
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.robold
import nova.composeapp.generated.resources.robothin
import okhttp3.OkHttpClient
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * **fontFamily** -> the Nova's font family
 */
lateinit var fontFamily: FontFamily

/**
 * **thinFontFamily** -> the Nova's thin font family
 */
lateinit var thinFontFamily: FontFamily

/**
 * **navigator** -> the navigator instance is useful to manage the navigation between the screens of the application
 */
lateinit var navigator: Navigator

/**
 * **sslContext** -> the context helper to TLS protocols
 */
private val sslContext = SSLContext.getInstance("TLS")

/**
 * **imageLoader** -> the image loader used by coil library to load the image and by-passing the https self-signed certificates
 */
lateinit var imageLoader: ImageLoader

@Composable
@Preview
fun App() {
    fontFamily = FontFamily(Font(Res.font.robold))
    thinFontFamily = FontFamily(Font(Res.font.robothin))
    sslContext.init(null, validateSelfSignedCertificate(), SecureRandom())
    imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
        .components {
            add(
                OkHttpNetworkFetcherFactory {
                    OkHttpClient.Builder()
                        .sslSocketFactory(
                            sslContext.socketFactory,
                            validateSelfSignedCertificate()[0] as X509TrustManager
                        )
                        .hostnameVerifier { _: String?, _: SSLSession? -> true }
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .build()
                }
            )
        }
        .addLastModifiedToFileCacheKey(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
    PreComposeApp {
        navigator = rememberNavigator()
        NovaTheme {
            NavHost(
                navigator = navigator,
                initialRoute = SPLASH_SCREEN
            ) {
                scene(
                    route = SPLASH_SCREEN
                ) {
                    SplashScreen().ShowContent()
                }
            }
        }
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