package com.example

import net.thauvin.erik.bitly.Bitly
import kotlin.system.exitProcess

fun main() {
    println(Bitly("YOUR_API_KEY").bitlinks().shorten("https://erik.thauvin.net/blog"))
    exitProcess(0)
}
