/*
 * UpdateConfig.kt
 *
 * Copyright (c) 2020-2022, Erik C. Thauvin (erik@thauvin.net)
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
    @Suppress("unused")
    data class Builder(
        var bitlink: String = Constants.EMPTY,
        var references: Map<String, String> = emptyMap(),
        var archived: Boolean = false,
        var tags: Array<String> = emptyArray(),
        var created_at: String = Constants.EMPTY,
        var title: String = Constants.EMPTY,
        var deeplinks: Array<Map<String, String>> = emptyArray(),
        var created_by: String = Constants.EMPTY,
        var long_url: String = Constants.EMPTY,
        var client_id: String = Constants.EMPTY,
        var custom_bitlinks: Array<String> = emptyArray(),
        var link: String = Constants.EMPTY,
        var id: String = Constants.EMPTY,
        var toJson: Boolean = false
    ) {
        fun bitlink(bitlink: String) = apply { this.bitlink = bitlink }
        fun references(references: Map<String, String>) = apply { this.references = references }
        fun archived(archived: Boolean) = apply { this.archived = archived }
        fun tags(tags: Array<String>) = apply { this.tags = tags }
        fun createdAt(created_at: String) = apply { this.created_at = created_at }
        fun title(title: String) = apply { this.title = title }
        fun deeplinks(deeplinks: Array<Map<String, String>>) = apply { this.deeplinks = deeplinks }
        fun createdBy(created_by: String) = apply { this.created_by = created_by }
        fun longUrl(long_url: String) = apply { this.long_url = long_url }
        fun clientId(client_id: String) = apply { this.client_id = client_id }
        fun customBitlinks(custom_bitlinks: Array<String>) = apply { this.custom_bitlinks = custom_bitlinks }
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Builder

            if (bitlink != other.bitlink) return false
            if (references != other.references) return false
            if (archived != other.archived) return false
            if (!tags.contentEquals(other.tags)) return false
            if (created_at != other.created_at) return false
            if (title != other.title) return false
            if (!deeplinks.contentEquals(other.deeplinks)) return false
            if (created_by != other.created_by) return false
            if (long_url != other.long_url) return false
            if (client_id != other.client_id) return false
            if (!custom_bitlinks.contentEquals(other.custom_bitlinks)) return false
            if (link != other.link) return false
            if (id != other.id) return false
            if (toJson != other.toJson) return false

            return true
        }

        override fun hashCode(): Int {
            var result = bitlink.hashCode()
            result = 31 * result + references.hashCode()
            result = 31 * result + archived.hashCode()
            result = 31 * result + tags.contentHashCode()
            result = 31 * result + created_at.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + deeplinks.contentHashCode()
            result = 31 * result + created_by.hashCode()
            result = 31 * result + long_url.hashCode()
            result = 31 * result + client_id.hashCode()
            result = 31 * result + custom_bitlinks.contentHashCode()
            result = 31 * result + link.hashCode()
            result = 31 * result + id.hashCode()
            result = 31 * result + toJson.hashCode()
            return result
        }
    }
}
