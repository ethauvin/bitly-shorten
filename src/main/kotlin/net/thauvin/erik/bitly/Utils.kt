/*
 * Utils.kt
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
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

/** Useful functions. */
class Utils private constructor() {
    companion object {
        /** The logger instance. **/
        val logger: Logger by lazy { Logger.getLogger(Bitly::class.java.simpleName) }

        private val client: OkHttpClient = if (logger.isLoggable(Level.FINE)) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
            }
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
        } else {
            OkHttpClient.Builder().build()
        }

        /**
         * Builds the full API endpoint URL using the [Constants.API_BASE_URL].
         *
         * @param endPointPath The REST request path. (eg. `/shorten', '/user`)
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
         * @param accessToken The API access token.
         * @param endPoint The REST endpoint. (eg. `https://api-ssl.bitly.com/v4/shorten`)
         * @param params The request parameters kev/value map.
         * @param method The submission [Method][Methods].
         * @return The response (JSON) from the API.
         */
        fun call(
            accessToken: String,
            endPoint: String,
            params: Map<String, String>,
            method: Methods = Methods.POST
        ): String {
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
            } catch (jse: JSONException) {
                logger.log(Level.SEVERE, "An error occurred parsing the error response from bitly.", jse)
            }
        }

        /**
         * Validates a URL.
         */
        fun validateUrl(url: String): Boolean {
            if (url.isNotBlank()) {
                try {
                    URL(url)
                    return true
                } catch (e: MalformedURLException) {
                    logger.log(Level.FINE, "Invalid URL: $url", e)
                }
            }
            return false
        }
    }
}
