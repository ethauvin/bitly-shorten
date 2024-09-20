/*
 * UpdateConfig.kt
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

package net.thauvin.erik.bitly.config

import net.thauvin.erik.bitly.Constants
import net.thauvin.erik.bitly.config.deeplinks.UpdateDeeplinks

/**
 * Provides a configuration to update a [Bitlink][net.thauvin.erik.bitly.Bitlinks].
 *
 * See the [Bit.ly API](https://dev.bitly.com/api-reference#updateBitlink) for more information.
 */
class UpdateConfig private constructor(builder: Builder) {
    val bitlink = builder.bitlink
    val title = builder.title
    val archived = builder.archived
    val tags = builder.tags
    val deeplinks = builder.deeplinks
    val toJson = builder.toJson

    /**
     * Configures the update parameters of a Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#updateBitlink) for more information.
     *
     * @param bitlink A Bitlink made of the domain and hash.
     **/
    data class Builder(var bitlink: String) {
        var title: String = Constants.EMPTY
        var archived: Boolean = false
        var tags: Array<String> = emptyArray()
        var deeplinks: UpdateDeeplinks = UpdateDeeplinks()
        var toJson: Boolean = false

        /**
         * A Bitlink made of the domain and hash.
         */
        fun bitlink(bitlink: String): Builder = apply { this.bitlink = bitlink }

        fun title(title: String): Builder = apply { this.title = title }
        fun archived(archived: Boolean): Builder = apply { this.archived = archived }
        fun tags(tags: Array<String>): Builder = apply { this.tags = tags }
        fun deeplinks(deeplinks: UpdateDeeplinks): Builder = apply { this.deeplinks = deeplinks }

        /**
         * Returns the full JSON response if `true`.
         */
        fun toJson(toJson: Boolean): Builder = apply { this.toJson = toJson }

        /**
         * Builds the configuration.
         */
        fun build() = UpdateConfig(this)
    }
}
