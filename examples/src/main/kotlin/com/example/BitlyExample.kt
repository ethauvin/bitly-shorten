package com.example

import net.thauvin.erik.bitly.Bitly
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        val bitly = Bitly(/* "YOUR_API_ACCESS_TOKEN from https://bitly.is/accesstoken" */)
        args.forEach {
            if (it.contains("bit.ly"))
                println(it + " <-- " + bitly.bitlinks().expand(it))
            else
                println(it + " --> " + bitly.bitlinks().shorten(it))
        }
    } else {
        println("Try specifying one or more URLs as arguments.")
    }
    exitProcess(0)
}
