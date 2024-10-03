package com.tecknobit.nova.helpers.utils

import android.content.Context
import androidx.startup.Initializer

/**
 * Initializes the `AppContext` during the application's startup phase using AndroidX's `Initializer` interface.
 *
 * This class is responsible for setting up the `AppContext` by providing the application-wide `Context` early
 * in the app's lifecycle. It is designed to work with AndroidX's `App Startup` library, which allows you to
 * initialize components as soon as the app starts, without having to modify the `Application` class.
 *
 * ## Usage:
 * - This class is automatically detected and executed by the `App Startup` library if properly declared in the
 *   app's manifest file or through metadata in the `AndroidManifest.xml`.
 * - It calls `AppContext.setUp()` to initialize the `AppContext` with the application `Context`.
 * - The `create()` method returns the global application `Context`, which can be used for further initialization
 *   or passed to other components that require the `Context`.
 * - Add the related provider in the **AndroidManifest.xml** file:
 * ```xml
 * <application>
 *
 *      ...
 *      <provider
 *                 android:name="androidx.startup.InitializationProvider"
 *                 android:authorities="${applicationId}.androidx-startup"
 *                 android:exported="false"
 *                 tools:node="merge">
 *             <meta-data
 *                     android:name="path.to.AppContextInitializer"
 *                     android:value="androidx.startup"/>
 *         </provider>
 *
 * </application>
 * ```
 *
 * @see Initializer
 */
internal class AppContextInitializer : Initializer<Context> {

    /**
     * Initializes the `AppContext` with the application-wide `Context`.
     *
     * This method is called by the AndroidX `App Startup` framework during the app's initialization phase.
     * It calls `AppContext.setUp()` to store the application `Context`, ensuring that the `AppContext` is
     * ready for use throughout the app's lifecycle.
     *
     * @param context the application `Context` provided by the system.
     * @return the global application `Context`, retrieved via `AppContext.get()`.
     */
    override fun create(
        context: Context
    ): Context {
        AppContext.setUp(context.applicationContext)
        return AppContext.get()
    }

    /**
     * Specifies dependencies for this initializer.
     *
     * This implementation has no dependencies on other initializers, so it returns an empty list.
     * If other initializers were needed to run before this one, they could be declared here.
     *
     * @return an empty list, indicating no dependencies.
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

