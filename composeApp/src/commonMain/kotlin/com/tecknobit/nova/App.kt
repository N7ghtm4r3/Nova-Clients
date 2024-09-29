package com.tecknobit.nova

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.addLastModifiedToFileCacheKey
import coil3.compose.LocalPlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import com.tecknobit.nova.theme.NovaTheme
import com.tecknobit.nova.ui.screens.NovaScreen
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.ADD_MEMBERS_DIALOG
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.ADD_MEMBERS_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.AUTH_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.JOIN_PROJECT_DIALOG
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.JOIN_PROJECT_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROFILE_DIALOG
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROFILE_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECTS_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.PROJECT_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.RELEASE_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.SPLASH_SCREEN
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.WORK_ON_PROJECT_DIALOG
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.WORK_ON_PROJECT_SCREEN
import com.tecknobit.nova.ui.screens.Splashscreen
import com.tecknobit.nova.ui.screens.addmembers.AddMembersScreen
import com.tecknobit.nova.ui.screens.auth.AuthScreen
import com.tecknobit.nova.ui.screens.joinproject.JoinProjectScreen
import com.tecknobit.nova.ui.screens.profile.ProfileScreen
import com.tecknobit.nova.ui.screens.project.ProjectScreen
import com.tecknobit.nova.ui.screens.projects.ProjectsScreen
import com.tecknobit.nova.ui.screens.release.ReleaseScreen
import com.tecknobit.nova.ui.screens.workonproject.WorkOnProjectDialog
import com.tecknobit.nova.ui.screens.workonproject.WorkOnProjectScreen
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
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
                    Splashscreen().ShowContent()
                }
                scene(
                    route = AUTH_SCREEN
                ) {
                    AuthScreen().ShowContent()
                }
                scene(
                    route = PROFILE_SCREEN
                ) {
                    ProfileScreen().ShowContent()
                }
                dialog(
                    route = PROFILE_DIALOG
                ) {
                    DialogScreen(
                        dialogScreen = ProfileScreen()
                    )
                }
                scene(
                    route = PROJECTS_SCREEN
                ) {
                    ProjectsScreen().ShowContent()
                }
                scene(
                    route = WORK_ON_PROJECT_SCREEN
                ) { backstackEntry ->
                    val project = backstackEntry.stateHolder.get<Project?>(PROJECT_KEY)
                    WorkOnProjectScreen(
                        project = project
                    ).ShowContent()
                }
                dialog(
                    route = WORK_ON_PROJECT_DIALOG
                ) { backstackEntry ->
                    val stateHolder = backstackEntry.stateHolder
                    val project = stateHolder.get<Project?>(PROJECT_KEY)
                    DialogScreen(
                        dialogScreen = WorkOnProjectDialog(
                            project = project
                        )
                    )
                    stateHolder.remove(PROJECT_KEY)
                }
                scene(
                    route = JOIN_PROJECT_SCREEN
                ) {
                    JoinProjectScreen(
                        enableScanOption = true
                    ).ShowContent()
                }
                dialog(
                    route = JOIN_PROJECT_DIALOG
                ) {
                    DialogScreen(
                        dialogScreen = JoinProjectScreen(
                            enableScanOption = false
                        )
                    )
                }
                scene(
                    route = "$PROJECT_SCREEN/{project_id}"
                ) { backstackEntry ->
                    val projectId = backstackEntry.path<String>(PROJECT_IDENTIFIER_KEY)!!
                    ProjectScreen(
                        projectId = projectId
                    ).ShowContent()
                }
                dialog(
                    route = ADD_MEMBERS_DIALOG
                ) { backstackEntry ->
                    val stateHolder = backstackEntry.stateHolder
                    val project = stateHolder.get<Project>(PROJECT_KEY)!!
                    DialogScreen(
                        dialogScreen = AddMembersScreen(
                            project = project
                        )
                    )
                    stateHolder.remove(PROJECT_KEY)
                }
                scene(
                    route = ADD_MEMBERS_SCREEN
                ) { backstackEntry ->
                    val project = backstackEntry.stateHolder.get<Project>(PROJECT_KEY)!!
                    AddMembersScreen(
                        project = project
                    ).ShowContent()
                }
                scene(
                    route = "$RELEASE_SCREEN/{project_id}/{release_id}"
                ) { backStackEntry ->
                    val projectId = backStackEntry.path<String>(PROJECT_IDENTIFIER_KEY)!!
                    val releaseId = backStackEntry.path<String>(RELEASE_IDENTIFIER_KEY)!!
                    ReleaseScreen(
                        projectId = projectId,
                        releaseId = releaseId
                    ).ShowContent()
                }
            }
        }
    }
}

@Composable
@NonRestartableComposable
private fun DialogScreen(
    dialogScreen: NovaScreen
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(
                    width = 425.dp,
                    height = 700.dp
                ),
            color = Color.Transparent,
            shadowElevation = 5.dp,
            shape = RoundedCornerShape(
                10.dp
            )
        ) {
            dialogScreen.ShowContent()
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