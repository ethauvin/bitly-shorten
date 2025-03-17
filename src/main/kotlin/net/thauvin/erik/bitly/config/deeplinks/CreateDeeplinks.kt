/*
 * CreateDeeplinks.kt
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

package net.thauvin.erik.bitly.config.deeplinks

import net.thauvin.erik.bitly.config.deeplinks.enums.InstallType
import java.util.*

/**
 * Configures deeplinks used when creating [Bitlinks][net.thauvin.erik.bitly.Bitlinks].
 *
 * See the [Bit.ly API](https://dev.bitly.com/api-reference#createFullBitlink) for more information.
 *
 * @since 2.0
 */
@Suppress("FunctionName", "LocalVariableName")
class CreateDeeplinks {
    private val map = mutableMapOf<String, String>()

    fun app_id(app_id: String) {
        map["app_id"] = app_id
    }

    fun app_id(): String? = map["app_id"]

    fun app_uri_path(app_uri_path: String) {
        map["app_uri_path"] = app_uri_path
    }

    fun app_uri_path(): String? = map["app_uri_path"]

    fun install_url(install_url: String) {
        map["install_url"] = install_url
    }

    fun install_url(): String? = map["install_url"]

    fun install_type(install_type: InstallType) {
        map["install_type"] = install_type.type
    }

    fun install_type(): InstallType? {
        val type = map["install_type"]
        if (type != null) {
            return InstallType.valueOf(type.uppercase(Locale.getDefault()))
        }
        return null
    }

    /**
     * Returns `true` if there are defined links.
     */
    fun isNotEmpty(): Boolean = map.isNotEmpty()

    /**
     * Returns the links.
     */
    fun links(): Map<String, String> = map
}
