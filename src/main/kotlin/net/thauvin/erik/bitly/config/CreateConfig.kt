/*
 * CreateConfig.kt
 *
 * Copyright 2020-2026 Erik C. Thauvin (erik@thauvin.net)
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
import net.thauvin.erik.bitly.config.deeplinks.CreateDeeplinks

/**
 * Provides a configuration to create a [Bitlink][net.thauvin.erik.bitly.Bitlinks]
 *
 * See the [Bit.ly API](https://dev.bitly.com/api-reference#createFullBitlink) for more information.
 */
@Suppress("LocalVariableName", "PropertyName")
class CreateConfig private constructor(builder: Builder) {
    val long_url = builder.long_url
    val domain = builder.domain
    val group_guid = builder.group_guid
    val title = builder.title
    val tags: List<String> = builder.tags.toList()
        get() = field.toList() // Return a defensive copy    val deeplinks = builder.deeplinks
    val deeplinks = builder.deeplinks
    val toJson = builder.toJson

    /**
     * Configures the creation parameters of a Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#createFullBitlink) for more information.
     *
     * @param long_url The long URL.
     **/
    data class Builder(var long_url: String) {
        private var _tags: List<String> = emptyList()

        var domain: String = Constants.EMPTY
        var group_guid: String = Constants.EMPTY
        var title: String = Constants.EMPTY
        val tags: List<String>
            get() = _tags.toList()
        var deeplinks: CreateDeeplinks = CreateDeeplinks()
        var toJson: Boolean = false

        /**
         * A branded short domain or `bit.ly` by default.
         */
        fun domain(domain: String): Builder = apply { this.domain = domain }

        /**
         * Always include a specific group and custom domain in your shorten calls.
         */
        fun groupGuid(group_guid: String): Builder = apply { this.group_guid = group_guid }

        fun title(title: String): Builder = apply { this.title = title }

        fun tags(tags: List<String>): Builder = apply { _tags = tags.toList() }

        fun deeplinks(deeplinks: CreateDeeplinks): Builder = apply { this.deeplinks = deeplinks }

        /**
         * The long URL.
         */
        fun longUrl(long_url: String): Builder = apply { this.long_url = long_url }

        /**
         * Returns the full JSON response if `true`.
         */
        fun toJson(toJson: Boolean): Builder = apply { this.toJson = toJson }

        /**
         * Builds the configuration.
         */
        fun build() = CreateConfig(this)
    }
}
