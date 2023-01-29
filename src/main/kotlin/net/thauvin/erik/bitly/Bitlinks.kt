/*
 * Bitlinks.kt
 *
 * Copyright 2020-2023 Erik C. Thauvin (erik@thauvin.net)
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

import net.thauvin.erik.bitly.Utils.Companion.isSevereLoggable
import net.thauvin.erik.bitly.Utils.Companion.isValidUrl
import net.thauvin.erik.bitly.Utils.Companion.removeHttp
import net.thauvin.erik.bitly.Utils.Companion.toEndPoint
import org.json.JSONException
import org.json.JSONObject
import java.util.logging.Level

/**
 * Provides functions to create and manage Bitlinks.
 *
 * See the [Bitly API](https://dev.bitly.com/api-reference) for more information.
 */
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
     * @param size The quantity of items to be be returned.
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
        size: Int = 50,
        unit_reference: String = Constants.EMPTY,
        toJson: Boolean = false
    ): String {
        var clicks = if (toJson) Constants.EMPTY_JSON else Constants.EMPTY
        if (bitlink.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken,
                ("/bitlinks/${bitlink.removeHttp()}/clicks/summary").toEndPoint(),
                mapOf(
                    "unit" to unit.toString().lowercase(),
                    "units" to units.toString(),
                    "size" to size.toString(),
                    "unit_reference" to unit_reference
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
     * @param domain A branded short domain or `bit.ly` by default.
     * @param group_guid A GUID for a Bitly group.
     * @param long_url The long URL.
     * @param toJson Returns the full JSON response if `true`.
     * @return The shorten URL or an empty string on error.
     */
    @Synchronized
    @JvmOverloads
    fun create(
        domain: String = Constants.EMPTY,
        title: String = Constants.EMPTY,
        group_guid: String = Constants.EMPTY,
        tags: Array<String> = emptyArray(),
        deeplinks: Array<Map<String, String>> = emptyArray(),
        long_url: String,
        toJson: Boolean = false
    ): String {
        var link = if (toJson) Constants.EMPTY_JSON else Constants.EMPTY
        if (long_url.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken,
                "/bitlinks".toEndPoint(),
                mutableMapOf<String, Any>("long_url" to long_url).apply {
                    if (domain.isNotBlank()) put("domain", domain)
                    if (title.isNotBlank()) put("title", title)
                    if (group_guid.isNotBlank()) put("group_guid", group_guid)
                    if (tags.isNotEmpty()) put("tags", tags)
                    if (deeplinks.isNotEmpty()) put("deeplinks", deeplinks)
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
                "/expand".toEndPoint(),
                mapOf("bitlink_id" to bitlink_id.removeHttp())
            )
            longUrl = parseJsonResponse(lastCallResponse, "long_url", longUrl, toJson)
        }

        return longUrl
    }

    private fun JSONObject.getString(key: String, default: String): String {
        return if (this.has(key))
            this.get(key).toString()
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
     * @param group_guid A GUID for a Bitly group.
     * @param domain A branded short domain or `bit.ly` by default.
     * @param toJson Returns the full JSON response if `true`.
     * @return The short URL or the [long_url] on error.
     */
    @Synchronized
    @JvmOverloads
    fun shorten(
        long_url: String,
        group_guid: String = Constants.EMPTY,
        domain: String = Constants.EMPTY,
        toJson: Boolean = false
    ): String {
        var bitlink = if (toJson) Constants.EMPTY_JSON else long_url
        if (!long_url.isValidUrl()) {
            if (Utils.logger.isSevereLoggable()) {
                Utils.logger.severe("Please specify a valid URL to shorten.")
            }
        } else {
            val params = mutableMapOf("long_url" to long_url).apply {
                if (group_guid.isNotBlank()) put("group_guid", group_guid)
                if (domain.isNotBlank()) put("domain", domain)
            }

            lastCallResponse = Utils.call(accessToken, "/shorten".toEndPoint(), params)

            bitlink = parseJsonResponse(lastCallResponse, "link", bitlink, toJson)
        }

        return bitlink
    }


    /**
     * Updates fields in the specified Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#updateBitlink) for more information.
     *
     * @param config The update configuration.
     * @return [Constants.TRUE] if the update was successful, [Constants.FALSE] otherwise.
     */
    @Synchronized
    fun update(config: UpdateConfig): String {
        return update(
            config.bitlink,
            config.references,
            config.archived,
            config.tags,
            config.created_at,
            config.title,
            config.deepLinks,
            config.created_by,
            config.long_url,
            config.client_id,
            config.custom_bitlinks,
            config.link,
            config.id,
            config.toJson
        )
    }

    /**
     * Updates fields in the specified Bitlink.
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
        references: Map<String, String> = emptyMap(),
        archived: Boolean = false,
        tags: Array<String> = emptyArray(),
        created_at: String = Constants.EMPTY,
        title: String = Constants.EMPTY,
        deeplinks: Array<Map<String, String>> = emptyArray(),
        created_by: String = Constants.EMPTY,
        long_url: String = Constants.EMPTY,
        client_id: String = Constants.EMPTY,
        custom_bitlinks: Array<String> = emptyArray(),
        link: String = Constants.EMPTY,
        id: String = Constants.EMPTY,
        toJson: Boolean = false
    ): String {
        var result = if (toJson) Constants.EMPTY_JSON else Constants.FALSE
        if (bitlink.isNotBlank()) {
            lastCallResponse = Utils.call(
                accessToken, "/bitlinks/${bitlink.removeHttp()}".toEndPoint(), mutableMapOf<String, Any>().apply {
                    if (references.isNotEmpty()) put("references", references)
                    if (archived) put("archived", true)
                    if (tags.isNotEmpty()) put("tags", tags)
                    if (created_at.isNotBlank()) put("created_at", created_at)
                    if (title.isNotBlank()) put("title", title)
                    if (deeplinks.isNotEmpty()) put("deeplinks", deeplinks)
                    if (created_by.isNotBlank()) put("created_by", created_by)
                    if (long_url.isNotBlank()) put("long_url", long_url)
                    if (client_id.isNotBlank()) put("client_id", client_id)
                    if (custom_bitlinks.isNotEmpty()) put("custom_bitlinks", custom_bitlinks)
                    if (link.isNotBlank()) put("link", link)
                    if (id.isNotBlank()) put("id", id)
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
}
