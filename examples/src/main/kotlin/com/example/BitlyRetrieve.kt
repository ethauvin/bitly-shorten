package com.example

import net.thauvin.erik.bitly.Bitly
import net.thauvin.erik.bitly.Methods
import net.thauvin.erik.bitly.Utils.Companion.toEndPoint
import org.json.JSONObject
import kotlin.system.exitProcess

fun main() {
    val bitly = Bitly(/* "YOUR_API_ACCESS_TOKEN from https://bitly.is/accesstoken" */)

    // See https://dev.bitly.com/v4/#operation/getBitlink
    val response = bitly.call("/bitlinks/bit.ly/380ojFd".toEndPoint(), method = Methods.GET)

    if (response.isSuccessful) {
        val json = JSONObject(response.body)
        println("Title : " + json.getString("title"))
        println("URL   : " + json.getString("long_url"))
        println("By    : " + json.getString("created_by"))
    } else {
        println("Invalid Response: ${response.resultCode}")
    }

    exitProcess(0)
}
