/*
 * CreateConfig.kt
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

package net.thauvin.erik.bitly.config

import net.thauvin.erik.bitly.Constants

/**
 * Provides a builder to create a Bitlink.
 */
class CreateConfig private constructor(
    val domain: String,
    val title: String,
    val group_guid: String,
    val tags: Array<String>,
    val deepLinks: Array<Map<String, String>>,
    val long_url: String,
    val toJson: Boolean
) {
    /**
     * Configures the creation parameters of a Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#createFullBitlink) for more information.
     **/
    @Suppress("unused", "ArrayInDataClass")
    data class Builder(
        var domain: String = Constants.EMPTY,
        var title: String = Constants.EMPTY,
        var group_guid: String = Constants.EMPTY,
        var tags: Array<String> = emptyArray(),
        var deeplinks: Array<Map<String, String>> = emptyArray(),
        var long_url: String = Constants.EMPTY,
        var toJson: Boolean = false
    ) {
        fun domain(domain: String) = apply { this.domain = domain }
        fun title(title: String) = apply { this.title = title }
        fun group_guid(group_guid: String) = apply { this.group_guid = group_guid }
        fun tags(tags: Array<String>) = apply { this.tags = tags }
        fun deeplinks(deeplinks: Array<Map<String, String>>) = apply { this.deeplinks = deeplinks }
        fun longUrl(long_url: String) = apply { this.long_url = long_url }
        fun toJson(toJson: Boolean) = apply { this.toJson = toJson }

        fun build() = CreateConfig(
            domain,
            title,
            group_guid,
            tags,
            deeplinks,
            long_url,
            toJson
        )
    }
}
