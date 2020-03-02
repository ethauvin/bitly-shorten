package com.example

import net.thauvin.erik.bitly.Bitly
import net.thauvin.erik.bitly.Methods
import net.thauvin.erik.bitly.Utils.Companion.toEndPoint
import org.json.JSONObject
import kotlin.system.exitProcess

fun main() {
    val bitly = Bitly(/* "YOUR_API_ACCESS_TOKEN from https://bitly.is/accesstoken" */)

    val json = JSONObject(bitly.call("/bitlinks/bit.ly/380ojFd".toEndPoint(), method = Methods.GET))

    println("Bitlink is titled : " + json.getString("title"))
    println("Bitlink created by: " + json.getString("created_by"))

    exitProcess(0)
}
                                   
