package com.tecknobit.nova.helpers.utils

import java.awt.Desktop
import java.net.URI

actual fun openLink(
    url: String
) {
    Desktop.getDesktop().browse(URI(url))
}