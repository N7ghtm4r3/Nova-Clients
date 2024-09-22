package com.tecknobit.nova

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform