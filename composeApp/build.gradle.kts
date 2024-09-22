import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.work.runtime)
            implementation("com.google.android.play:review:2.0.1")
            implementation("com.google.android.play:review-ktx:2.0.1")
            implementation("com.google.android.play:app-update:2.1.0")
            implementation("com.google.android.play:app-update-ktx:2.1.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            api("moe.tlaster:precompose:1.6.2")
            implementation("com.github.N7ghtm4r3:APIManager:2.2.3")
            implementation("com.tecknobit.novacore:novacore:1.0.1")
            implementation("com.github.N7ghtm4r3:Equinox:1.0.3")
            implementation("io.github.n7ghtm4r3:Equinox-Compose:1.0.1")
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha07")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-alpha07")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.tecknobit.nova"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.tecknobit.nova"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.0.1"
    }

    packaging {
        resources {
            excludes += "**/*"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(Deb, Pkg, Exe)
            modules("java.compiler", "java.instrument", "java.management", "java.naming", "java.net.http", "java.prefs",
                "java.rmi", "java.scripting", "java.sql", "jdk.jfr", "jdk.unsupported")
            packageName = "Nova"
            packageVersion = "1.0.1"
            packageName = "Nova"
            packageVersion = "1.0.1"
            description = "Nova, open source tool to manage and improve the developments of your releases"
            copyright = "© 2024 Tecknobit"
            vendor = "Tecknobit"
            licenseFile.set(project.file("LICENSE"))
            macOS {
                bundleID = "com.tecknobit.nova"
                iconFile.set(project.file("src/commonMain/resources/logo.icns"))
            }
            windows {
                iconFile.set(project.file("src/commonMain/resources/logo.ico"))
                upgradeUuid = UUID.randomUUID().toString()
            }
            linux {
                iconFile.set(project.file("src/commonMain/resources/logo.png"))
                packageName = "com-tecknobit-nova"
                debMaintainer = "infotecknobitcompany@gmail.com"
                appRelease = "1.0.1"
                appCategory = "PERSONALIZATION"
                rpmLicenseType = "MIT"
            }
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
            version.set("7.5.0")
            obfuscate.set(true)
        }
    }
}

configurations.all {
    exclude("commons-logging", "commons-logging")
}
