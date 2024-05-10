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

/**
 * Provides a builder to update a Bitlink.
 */
class UpdateConfig private constructor(
    val bitlink: String,
    val references: Map<String, String>,
    val archived: Boolean,
    val tags: Array<String>,
    val created_at: String,
    val title: String,
    val deepLinks: Array<Map<String, String>>,
    val created_by: String,
    val long_url: String,
    val client_id: String,
    val custom_bitlinks: Array<String>,
    val link: String,
    val id: String,
    val toJson: Boolean,
) {
    /**
     * Configures the update parameters of a Bitlink.
     *
     * See the [Bit.ly API](https://dev.bitly.com/api-reference#updateBitlink) for more information.
     **/
    @Suppress("ArrayInDataClass")
    data class Builder(
        private var bitlink: String = Constants.EMPTY,
        private var references: Map<String, String> = emptyMap(),
        private var archived: Boolean = false,
        private var tags: Array<String> = emptyArray(),
        private var created_at: String = Constants.EMPTY,
        private var title: String = Constants.EMPTY,
        private var deeplinks: Array<Map<String, String>> = emptyArray(),
        private var created_by: String = Constants.EMPTY,
        private var long_url: String = Constants.EMPTY,
        private var client_id: String = Constants.EMPTY,
        private var custom_bitlinks: Array<String> = emptyArray(),
        private var link: String = Constants.EMPTY,
        private var id: String = Constants.EMPTY,
        private var toJson: Boolean = false
    ) {
        fun bitlink(bitlink: String) = apply { this.bitlink = bitlink }
        fun references(references: Map<String, String>) = apply { this.references = references }
        fun archived(archived: Boolean) = apply { this.archived = archived }
        fun tags(tags: Array<String>) = apply { this.tags = tags }
        fun createdAt(createdAt: String) = apply { this.created_at = createdAt }
        fun title(title: String) = apply { this.title = title }
        fun deepLinks(deepLinks: Array<Map<String, String>>) = apply { this.deeplinks = deepLinks }
        fun createdBy(createdBy: String) = apply { this.created_by = createdBy }
        fun longUrl(longUrl: String) = apply { this.long_url = longUrl }
        fun clientId(clientId: String) = apply { this.client_id = clientId }
        fun customBitlinks(customBitlinks: Array<String>) = apply { this.custom_bitlinks = customBitlinks }
        fun link(link: String) = apply { this.link = link }
        fun id(id: String) = apply { this.id = id }
        fun toJson(toJson: Boolean) = apply { this.toJson = toJson }

        fun build() = UpdateConfig(
            bitlink,
            references,
            archived,
            tags,
            created_at,
            title,
            deeplinks,
            created_by,
            long_url,
            client_id,
            custom_bitlinks,
            link,
            id,
            toJson
        )
    }
}
