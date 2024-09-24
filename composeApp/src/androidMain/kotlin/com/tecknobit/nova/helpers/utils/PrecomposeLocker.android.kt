package com.tecknobit.nova.helpers.utils

private var locker = 0

actual fun waitToStart(
    action: () -> Unit
) {
    if(locker == 3) {
        action.invoke()
        locker = 0
    } else
        locker++
}