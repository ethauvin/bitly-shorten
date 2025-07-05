/*
 * Bitlinks.kt
 *
 * Copyright 2020-2025 Erik C. Thauvin (erik@thauvin.net)
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

import net.thauvin.erik.bitly.Utils.isSevereLoggable
import net.thauvin.erik.bitly.Utils.isValidUrl
import net.thauvin.erik.bitly.Utils.removeHttp
import net.thauvin.erik.bitly.Utils.toEndPoint
import net.thauvin.erik.bitly.config.CreateConfig
import net.thauvin.erik.bitly.config.UpdateConfig
import net.thauvin.erik.bitly.config.deeplinks.CreateDeeplinks
import net.thauvin.erik.bitly.config.deeplinks.UpdateDeeplinks
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.logging.Level

/**
 * Provides functions to create and manage Bitlinks.
 *
 * See the [Bitly API](https://dev.bitly.com/api-reference) for more information.
 */
@Suppress("LocalVariableName")
open class Bitlinks(private val accessToken: String) {
    /**
     * The last API call response.
     */
    var lastCallResponse = CallResponse()
        private set

    /**
     * Returns the click counts for a specified Bitlink.
     *
     * See the [Bitly API](https://dev.bitly.com/api-reference#getClicksSummaryForBitlink) for more information.
     *
     * @param bitlink A Bitlink made of the domain and hash.
     * @param unit A [unit of time][Units].
     * @param units An integer representing the time units to query data for. Pass -1 to return all units available.
     * @param unit_reference An ISO-8601 timestamp, indicating the most recent time for which to pull metrics.
     * Will default to current time.
     * @param toJson Returns the full JSON response if `true`.
     * @return The click counts.
     */
    @Synchronized
    @JvmOverloads
    fun clicks(
        bitlink: String,
        unit: Units = Units.DAY,
        units: Int = -1,
        unit_reference: String = Constants.EMPTY,
        toJson: Boolean = false
    ): String {
        var clicks = if (toJson) Constants.EMPTY_JSON else Constants.EMPTY
        if (bitlink.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken,
                ("bitlinks/${bitlink.removeHttp()}/clicks/summary").toEndPoint(),
                mapOf(
                    "unit" to unit.toString().lowercase(),
                    "units" to units.toString(),
                    "unit_reference" to URLEncoder.encode(unit_reference, StandardCharsets.UTF_8)
                ),
                Methods.GET
            )
            clicks = parseJsonResponse(lastCallResponse, "total_clicks", clicks, toJson)
        }
        return clicks
    }

    /**
     * Converts a long url to a Bitlink and sets additional parameters.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#createFullBitlink) for more information.
     *
     * @param config The parameters' configuration.
     * @return The shortened URL or an empty string on error.
     */
    @Synchronized
    fun create(config: CreateConfig): String {
        return create(
            config.long_url,
            config.domain,
            config.group_guid,
            config.title,
            config.tags,
            config.deeplinks,
            config.toJson
        )
    }

    /**
     * Converts a long url to a Bitlink and sets additional parameters.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#createFullBitlink) for more information.
     *
     * @param long_url The long URL.
     * @param domain A branded short domain or `bit.ly` by default.
     * @param group_guid A GUID for a Bitly group.
     * @param toJson Returns the full JSON response if `true`.
     * @return The shortened URL or an empty string on error.
     */
    @Synchronized
    @JvmOverloads
    fun create(
        long_url: String,
        domain: String = Constants.EMPTY,
        group_guid: String = Constants.EMPTY,
        title: String = Constants.EMPTY,
        tags: List<String> = emptyList(),
        deeplinks: CreateDeeplinks = CreateDeeplinks(),
        toJson: Boolean = false
    ): String {
        var link = if (toJson) Constants.EMPTY_JSON else Constants.EMPTY
        if (long_url.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken,
                "bitlinks".toEndPoint(),
                mutableMapOf<String, Any>("long_url" to long_url).apply {
                    if (domain.isNotBlank()) put("domain", domain)
                    if (group_guid.isNotBlank()) put("group_guid", group_guid)
                    if (title.isNotBlank()) put("title", title)
                    if (tags.isNotEmpty()) put("tags", tags)
                    if (deeplinks.isNotEmpty()) put("deeplinks", arrayOf(deeplinks.links()))
                }
            )
            link = parseJsonResponse(lastCallResponse, "link", link, toJson)
        }
        return link
    }

    /**
     * Expands a Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#expandBitlink) for more information.
     *
     * @param bitlink_id The bitlink ID.
     * @param toJson Returns the full JSON response if `true`.
     * @return The long URL or an empty string on error.
     */
    @Synchronized
    @JvmOverloads
    fun expand(bitlink_id: String, toJson: Boolean = false): String {
        var longUrl = if (toJson) Constants.EMPTY_JSON else Constants.EMPTY
        if (bitlink_id.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken,
                "expand".toEndPoint(),
                mapOf("bitlink_id" to bitlink_id.removeHttp())
            )
            longUrl = parseJsonResponse(lastCallResponse, "long_url", longUrl, toJson)
        }

        return longUrl
    }

    private fun JSONObject.getString(key: String, default: String): String {
        return if (this.has(key))
            this[key].toString()
        else
            default
    }

    private fun parseJsonResponse(response: CallResponse, key: String, default: String, toJson: Boolean): String {
        var parsed = default
        if (response.body.isNotEmpty()) {
            if (toJson) {
                parsed = response.body
            } else {
                try {
                    parsed = JSONObject(response.body).getString(key, default)
                } catch (jse: JSONException) {
                    if (Utils.logger.isSevereLoggable()) {
                        Utils.logger.log(Level.SEVERE, "An error occurred parsing the response from Bitly.", jse)
                    }
                }
            }
        }
        return parsed
    }

    /**
     * Shortens a long URL.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#createBitlink) for more information.
     *
     * @param long_url The long URL.
     * @param domain A branded short domain or `bit.ly` by default.
     * @param group_guid A GUID for a Bitly group.
     * @param toJson Returns the full JSON response if `true`.
     * @return The short URL or the [long_url] on error.
     */
    @Synchronized
    @JvmOverloads
    fun shorten(
        long_url: String,
        domain: String = Constants.EMPTY,
        group_guid: String = Constants.EMPTY,
        toJson: Boolean = false
    ): String {
        var bitlink = if (toJson) Constants.EMPTY_JSON else long_url
        if (!long_url.isValidUrl()) {
            if (Utils.logger.isSevereLoggable()) {
                Utils.logger.severe("Please specify a valid URL to shorten.")
            }
        } else {
            val params = mutableMapOf("long_url" to long_url).apply {
                if (domain.isNotBlank()) put("domain", domain)
                if (group_guid.isNotBlank()) put("group_guid", group_guid)
            }

            lastCallResponse = Utils.call(accessToken, "shorten".toEndPoint(), params)

            bitlink = parseJsonResponse(lastCallResponse, "link", bitlink, toJson)
        }

        return bitlink
    }


    /**
     * Updates parameters in the specified Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#updateBitlink) for more information.
     *
     * @param config The parameters' configuration.
     * @return [Constants.TRUE] if the update was successful, [Constants.FALSE] otherwise.
     */
    @Synchronized
    fun update(config: UpdateConfig): String {
        return update(
            config.bitlink,
            config.title,
            config.archived,
            config.tags,
            config.deeplinks,
            config.toJson
        )
    }

    /**
     * Updates parameters in the specified Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#updateBitlink) for more information.
     *
     * @param bitlink A Bitlink made of the domain and hash.
     * @param toJson Returns the full JSON response if `true`.
     * @return [Constants.TRUE] if the update was successful, [Constants.FALSE] otherwise.
     */
    @Synchronized
    @JvmOverloads
    fun update(
        bitlink: String,
        title: String = Constants.EMPTY,
        archived: Boolean = false,
        tags: List<String> = emptyList(),
        deeplinks: UpdateDeeplinks = UpdateDeeplinks(),
        toJson: Boolean = false
    ): String {
        var result = if (toJson) Constants.EMPTY_JSON else Constants.FALSE
        if (bitlink.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken, "bitlinks/${bitlink.removeHttp()}".toEndPoint(), mutableMapOf<String, Any>().apply {
                    if (title.isNotBlank()) put("title", title)
                    if (archived) put("archived", true)
                    if (tags.isNotEmpty()) put("tags", tags)
                    if (deeplinks.isNotEmpty()) put("deeplinks", arrayOf(deeplinks.links()))
                },
                Methods.PATCH
            )

            if (lastCallResponse.isSuccessful) {
                result = if (toJson) {
                    lastCallResponse.body
                } else {
                    Constants.TRUE
                }
            }
        }
        return result
    }

    /**
     * Move a keyword (or custom back-half) to a different Bitlink (domains must match).
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference/#updateCustomBitlink) for more information.
     *
     * @param custom_bitlink A Custom Bitlink made of the domain and keyword.
     * @param toJson Returns the full JSON response if `true`.
     * @return [Constants.TRUE] if the update was successful, [Constants.FALSE] otherwise.
     */
    @Synchronized
    @JvmOverloads
    fun updateCustom(
        custom_bitlink: String,
        bitlink_id: String,
        toJson: Boolean = false
    ): String {
        var result = if (toJson) Constants.EMPTY_JSON else Constants.FALSE
        if (custom_bitlink.isNotBlank() && bitlink_id.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken,
                "custom_bitlinks/${custom_bitlink.removeHttp()}".toEndPoint(),
                mutableMapOf<String, Any>().apply { put("bitlink_id", bitlink_id) },
                Methods.PATCH
            )

            if (lastCallResponse.isSuccessful) {
                result = if (toJson) {
                    lastCallResponse.body
                } else {
                    Constants.TRUE
                }
            }
        }
        return result
    }
}
