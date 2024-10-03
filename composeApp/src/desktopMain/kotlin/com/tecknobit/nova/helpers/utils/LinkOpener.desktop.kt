package com.tecknobit.nova.helpers.utils

import java.awt.Desktop
import java.net.URI

/**
 * Function to open a link from **Desktop** platform
 *
 * @param url: the ulr of the link to open
 */
actual fun openLink(
    url: String
) {
    Desktop.getDesktop().browse(URI(url))
}