package com.tecknobit.nova.helpers.utils

private var locker = 0

actual fun waitToStart(
    action: () -> Unit
) {
    if(locker == 4) {
        action.invoke()
        locker = 0
    } else
        locker++
}