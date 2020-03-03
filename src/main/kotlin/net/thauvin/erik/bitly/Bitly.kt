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

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

/**
 * Provides access to the [Bitly API v4](https://dev.bitly.com/v4).
 *
 * @constructor Creates new instance.
 */
open class Bitly() {
    /** The API access token.
     *
     * See [Generic Access Token](https://bitly.is/accesstoken) or
     * [Authentication](https://dev.bitly.com/v4/#section/Authentication).
     **/
    var accessToken: String = System.getenv(Constants.ENV_ACCESS_TOKEN)
        ?: (System.getProperty(Constants.ENV_ACCESS_TOKEN) ?: Constants.EMPTY)

    /**
     * Creates a new instance using an [API Access Token][Bitly.accessToken].
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
     * @param key The property key containing the [API Access Token][accessToken].
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
     * @param key The property key containing the [API Access Token][accessToken].
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
     * @param key The property key containing the [API Access Token][accessToken].
     */
    @Suppress("unused")
    @JvmOverloads
    constructor(propertiesFile: File, key: String = Constants.ENV_ACCESS_TOKEN) : this(propertiesFile.toPath(), key)

    /**
     * Returns a new [Bitlinks] instance.
     */
    fun bitlinks(): Bitlinks = Bitlinks(accessToken)

    /**
     * Executes an API call.
     *
     * @param endPoint The REST endpoint. (eg. `https://api-ssl.bitly.com/v4/shorten`)
     * @param params The request parameters key/value map.
     * @param method The submission [Method][Methods].
     * @return The response (JSON) from the API.
     */
    @JvmOverloads
    fun call(endPoint: String, params: Map<String, Any> = emptyMap(), method: Methods = Methods.POST): CallResponse {
        return Utils.call(accessToken, endPoint, params, method)
    }
}
