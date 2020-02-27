/*
 * Bitlinks.kt
 *
 * Copyright (c) 2020, Erik C. Thauvin (erik@thauvin.net)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *   Neither the name of this project nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.thauvin.erik.bitly

import org.json.JSONException
import org.json.JSONObject
import java.util.logging.Level

/**
 * Bitlinks methods implementation.
 *
 * See the [Bitly API](https://dev.bitly.com/v4/#tag/Bitlinks) for more information.
 */
open class Bitlinks(val accessToken: String) {
    inner class Clicks {
        fun summary(
            bitlink: String,
            unit: Units = Units.DAY,
            units: Int = -1,
            size: Int = 50,
            unit_reference: String = Constants.EMPTY,
            isJson: Boolean = false
        ): String {
            var clicks = if (isJson) "{}" else Constants.EMPTY
            if (bitlink.isNotBlank()) {
                val response = Utils.call(
                    accessToken,
                    Utils.buildEndPointUrl("/bitlinks/" + bitlink.removeHttp() + "/clicks/summary"),
                    hashMapOf(
                        Pair("unit", unit.toString().toLowerCase()),
                        Pair("units", units.toString()),
                        Pair("size", size.toString()),
                        Pair("unit_reference", unit_reference)
                    ),
                    Methods.GET
                )
                clicks = parseJsonResponse(response, "total_clicks", clicks, isJson)
            }
            return clicks
        }
    }

    /**
     * Expands a Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/v4/#operation/expandBitlink) for more information.
     *
     * @param bitlink_id The bitlink ID.
     * @param isJson Returns the full JSON response if `true`
     * @return The long URL or JSON response, or on error, an empty string/JSON object.
     */
    @JvmOverloads
    fun expand(bitlink_id: String, isJson: Boolean = false): String {
        var longUrl = if (isJson) "{}" else Constants.EMPTY
        if (bitlink_id.isNotBlank()) {
            val response = Utils.call(
                accessToken,
                Utils.buildEndPointUrl("/expand"),
                mapOf(Pair("bitlink_id", bitlink_id.removeHttp())),
                Methods.POST
            )
            longUrl = parseJsonResponse(response, "long_url", longUrl, isJson)
        }

        return longUrl
    }

    private fun JSONObject.getString(key: String, default: String): String {
        return if (this.has(key))
            this.get(key).toString()
        else
            default
    }

    private fun parseJsonResponse(response: String, key: String, default: String, isJson: Boolean): String {
        var parsed = default
        if (response.isNotEmpty()) {
            if (isJson) {
                parsed = response
            } else {
                try {
                    parsed = JSONObject(response).getString(key, default)
                } catch (jse: JSONException) {
                    Utils.logger.log(Level.SEVERE, "An error occurred parsing the response from Bitly.", jse)
                }
            }
        }
        return parsed
    }

    private fun String.removeHttp(): String {
        return this.replaceFirst(Regex("^[Hh][Tt]{2}[Pp][Ss]?://"), "")
    }

    /**
     * Shortens a long URL.
     *
     * See the [Bit.ly API](https://dev.bitly.com/v4/#operation/createBitlink) for more information.
     *
     * @param long_url The long URL.
     * @param group_guid The group UID.
     * @param domain The domain for the short URL.
     * @param isJson Returns the full JSON response if `true`
     * @return The short URL or JSON response, or on error, the [long_url] or an empty JSON object.
     */
    @JvmOverloads
    fun shorten(
        long_url: String,
        group_guid: String = Constants.EMPTY,
        domain: String = Constants.EMPTY,
        isJson: Boolean = false
    ): String {
        var bitlink = if (isJson) "{}" else long_url
        if (!Utils.validateUrl(long_url)) {
            Utils.logger.severe("Please specify a valid URL to shorten.")
        } else {
            val params: HashMap<String, String> = HashMap()
            if (group_guid.isNotBlank()) {
                params["group_guid"] = group_guid
            }
            if (domain.isNotBlank()) {
                params["domain"] = domain
            }
            params["long_url"] = long_url

            val response = Utils.call(accessToken, Utils.buildEndPointUrl("/shorten"), params)

            bitlink = parseJsonResponse(response, "link", bitlink, isJson)
        }

        return bitlink
    }
}
