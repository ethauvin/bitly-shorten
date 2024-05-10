/*
 * Utils.kt
 *
 * Copyright 2020-2024 Erik C. Thauvin (erik@thauvin.net)
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

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Provides useful generic functions.
 */
object Utils {
    /**
     *  The logger instance.
     */
    @JvmStatic
    val logger: Logger by lazy { Logger.getLogger(Utils::class.java.name) }

    /**
     * Executes an API call.
     *
     * @param accessToken The API access token.
     * @param endPoint The REST endpoint URI. (eg. `https://api-ssl.bitly.com/v4/shorten`)
     * @param params The request parameters key/value map.
     * @param method The submission [Method][Methods].
     * @return A [CallResponse] object.
     */
    @JvmStatic
    @JvmOverloads
    fun call(
        accessToken: String,
        endPoint: String,
        params: Map<String, Any> = emptyMap(),
        method: Methods = Methods.POST
    ): CallResponse {
        require(endPoint.isNotBlank()) { "A valid API endpoint must be specified." }
        require(accessToken.isNotBlank()) { "A valid API access token must be provided." }

        endPoint.toHttpUrl().let { apiUrl ->
            val builder = when (method) {
                Methods.POST, Methods.PATCH -> {
                    val formBody = JSONObject(params).toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    Request.Builder().apply {
                        url(apiUrl.newBuilder().build())
                        if (method == Methods.POST) {
                            post(formBody)
                        } else {
                            patch(formBody)
                        }
                    }
                }

                Methods.DELETE -> Request.Builder().url(apiUrl.newBuilder().build()).delete()
                else -> { // Methods.GET
                    val httpUrl = apiUrl.newBuilder().apply {
                        params.forEach {
                            if (it.value is String) {
                                addQueryParameter(it.key, it.value.toString())
                            }
                        }
                    }.build()
                    Request.Builder().url(httpUrl)
                }
            }.addHeader("Authorization", "Bearer $accessToken")

            newHttpClient().newCall(builder.build()).execute().use {
                return parseResponse(it, endPoint)
            }
        }
    }

    private fun newHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (logger.isLoggable(Level.FINE)) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                    redactHeader("Authorization")
                })
            }
        }.build()
    }

    private fun parseResponse(response: Response, endPoint: String): CallResponse {
        var message = response.message
        var description = ""
        var json = Constants.EMPTY_JSON
        response.body?.string()?.let { body ->
            json = body
            if (!response.isSuccessful && body.isNotEmpty()) {
                try {
                    with(JSONObject(body)) {
                        if (has("message")) {
                            message = getString("message")
                        }
                        if (has("description")) {
                            description = getString("description")
                        }
                    }
                } catch (jse: JSONException) {
                    if (logger.isSevereLoggable()) {
                        logger.log(
                            Level.SEVERE,
                            "An error occurred parsing the error response from Bitly. [$endPoint]",
                            jse
                        )
                    }
                }
            }
        }
        response.close()
        return CallResponse(json, message, description, response.code)
    }

    /**
     * Determines if [Level.SEVERE] logging is enabled.
     */
    fun Logger.isSevereLoggable(): Boolean = this.isLoggable(Level.SEVERE)

    /**
     * Validates a URL.
     */
    @JvmStatic
    fun String.isValidUrl(): Boolean {
        if (this.isNotBlank()) {
            try {
                URI(this)
                return true
            } catch (e: URISyntaxException) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Invalid URL: $this", e)
                }
            }
        }
        return false
    }

    /**
     * Removes http(s) scheme from string.
     */
    @JvmStatic
    fun String.removeHttp(): String {
        return this.replaceFirst("^[Hh][Tt]{2}[Pp][Ss]?://".toRegex(), "")
    }

    /**
     * Builds the full API endpoint URL using the [Constants.API_BASE_URL].
     */
    @JvmStatic
    fun String.toEndPoint(): String {
        return if (this.isBlank() || this.startsWith("http", true)) {
            this
        } else {
            "${Constants.API_BASE_URL}/${this.removePrefix("/")}"
        }
    }
}
