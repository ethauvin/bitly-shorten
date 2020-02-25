/*
 * Bitly.kt
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

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The HTTP methods.
 */
enum class Methods {
    DELETE, GET, PATCH, POST
}

/**
 * A simple implementation of the Bitly API v4.
 *
 * @constructor Creates new instance.
 */
open class Bitly() {
    /** Constants for this package. **/
    object Constants {
        /** The Bitly API base URL. **/
        const val API_BASE_URL = "https://api-ssl.bitly.com/v4"

        /** The API access token environment variable. **/
        const val ENV_ACCESS_TOKEN = "BITLY_ACCESS_TOKEN"
    }

    /** The API access token. **/
    var accessToken: String = System.getenv(Constants.ENV_ACCESS_TOKEN) ?: ""

    /** The logger instance. **/
    val logger: Logger by lazy { Logger.getLogger(Bitly::class.java.simpleName) }

    private var client: OkHttpClient

    init {
        client = if (logger.isLoggable(Level.FINE)) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
            }
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
        } else {
            OkHttpClient.Builder().build()
        }
    }

    /**
     * Creates a new instance using an [API Access Token][accessToken].
     *
     * @param accessToken The API access token.
     */
    @Suppress("unused")
    constructor(accessToken: String) : this() {
        this.accessToken = accessToken
    }

    /**
     * Creates a new instance using a [Properties][properties] and [Property Key][key].
     *
     * @param properties The properties.
     * @param key The property key.
     */
    @Suppress("unused")
    @JvmOverloads
    constructor(properties: Properties, key: String = Constants.ENV_ACCESS_TOKEN) : this() {
        accessToken = properties.getProperty(key, accessToken)
    }

    /**
     * Creates a new instance using a [Properties File Path][propertiesFilePath] and [Property Key][key].
     *
     * @param propertiesFilePath The properties file path.
     * @param key The property key.
     */
    @JvmOverloads
    constructor(propertiesFilePath: Path, key: String = Constants.ENV_ACCESS_TOKEN) : this() {
        if (Files.exists(propertiesFilePath)) {
            accessToken = Properties().apply {
                Files.newInputStream(propertiesFilePath).use { nis ->
                    load(nis)
                }
            }.getProperty(key, accessToken)
        }
    }

    /**
     * Creates a new instance using a [Properties File][propertiesFile] and [Property Key][key].
     *
     * @param propertiesFile The properties file.
     * @param key The property key.
     */
    @Suppress("unused")
    @JvmOverloads
    constructor(propertiesFile: File, key: String = Constants.ENV_ACCESS_TOKEN) : this(propertiesFile.toPath(), key)

    /**
     * Builds the full API endpoint URL using the [Constants.API_BASE_URL].
     *
     * @param endPointPath The REST method path. (eg. `/shorten', '/user`)
     */
    fun buildEndPointUrl(endPointPath: String): String {
        return if (endPointPath.startsWith('/')) {
            "${Constants.API_BASE_URL}$endPointPath"
        } else {
            "${Constants.API_BASE_URL}/$endPointPath"
        }
    }

    /**
     * Executes an API call.
     *
     * @param endPoint The API endpoint. (eg. `/shorten`, `/user`)
     * @param params The request parameters kev/value map.
     * @param method The submission [Method][Methods].
     * @return The response (JSON) from the API.
     */
    fun executeCall(endPoint: String, params: Map<String, String>, method: Methods = Methods.POST): String {
        var response = ""
        if (endPoint.isBlank()) {
            logger.severe("Please specify a valid API endpoint.")
        } else if (accessToken.isBlank()) {
            logger.severe("Please specify a valid API access token.")
        } else {
            val apiUrl = endPoint.toHttpUrlOrNull()
            if (apiUrl != null) {
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
                    else -> {
                        val httpUrl = apiUrl.newBuilder().apply {
                            params.forEach {
                                addQueryParameter(it.key, it.value)
                            }
                        }.build()
                        Request.Builder().url(httpUrl)
                    }
                }.addHeader("Authorization", "Bearer $accessToken")

                val result = client.newCall(builder.build()).execute()

                val body = result.body?.string()
                if (body != null) {
                    if (!result.isSuccessful && body.isNotEmpty()) {
                        logApiError(body, result.code)
                    }
                    response = body
                }
            }
        }

        return response
    }

    /**
     * Shortens a long URL.
     *
     * See the [Bit.ly API](https://dev.bitly.com/v4/#operation/createBitlink) for more information.
     *
     * @param long_url The long URL.
     * @param group_guid The group UID.
     * @param domain The domain, defaults to `bit.ly`.
     * @param isJson Returns the full JSON API response if `true`
     * @return THe short URL or JSON API response.
     */
    @JvmOverloads
    fun shorten(long_url: String, group_guid: String = "", domain: String = "", isJson: Boolean = false): String {
        var bitlink = if (isJson) "{}" else ""
        if (!validateUrl(long_url)) {
            logger.severe("Please specify a valid URL to shorten.")
        } else {
            val params: HashMap<String, String> = HashMap()
            if (group_guid.isNotBlank()) {
                params["group_guid"] = group_guid
            }
            if (domain.isNotBlank()) {
                params["domain"] = domain
            }
            params["long_url"] = long_url

            val response = executeCall(buildEndPointUrl("/shorten"), params)

            if (response.isNotEmpty()) {
                if (isJson) {
                    bitlink = response
                } else {
                    try {
                        val json = JSONObject(response)
                        if (json.has("link")) {
                            bitlink = json.getString("link")
                        }
                    } catch (ignore: JSONException) {
                        logger.severe("An error occurred parsing the response from bitly.")
                    }
                }
            }
        }

        return bitlink
    }

    private fun logApiError(body: String, resultCode: Int) {
        try {
            with(JSONObject(body)) {
                if (has("message")) {
                    logger.severe(getString("message") + " ($resultCode)")
                }
                if (has("description")) {
                    val description = getString("description")
                    if (description.isNotBlank()) {
                        logger.severe(description)
                    }
                }
            }
        } catch (ignore: JSONException) {
            logger.severe("An error occurred parsing the error response from bitly.")
        }
    }

    private fun validateUrl(url: String): Boolean {
        var isValid = url.isNotBlank()
        if (isValid) {
            try {
                URL(url)
            } catch (e: MalformedURLException) {
                logger.log(Level.FINE, "Invalid URL: $url", e)
                isValid = false
            }
        }
        return isValid
    }
}
