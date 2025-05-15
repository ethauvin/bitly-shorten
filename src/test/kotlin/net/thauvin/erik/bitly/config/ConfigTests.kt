/*
 * ConfigTests.kt
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

package net.thauvin.erik.bitly.config

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import assertk.assertions.prop
import net.thauvin.erik.bitly.Constants
import net.thauvin.erik.bitly.config.deeplinks.CreateDeeplinks
import net.thauvin.erik.bitly.config.deeplinks.UpdateDeeplinks
import net.thauvin.erik.bitly.config.deeplinks.enums.InstallType
import net.thauvin.erik.bitly.config.deeplinks.enums.Os
import org.json.JSONObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class ConfigTests {
    @Nested
    @DisplayName("Build Configuration Tests")
    inner class BuildConfigurationTests {
        @Test
        fun `Build create configuration`() {
            val deeplinks = CreateDeeplinks().apply {
                app_id("app_id")
                install_type(InstallType.AUTO_INSTALL)
            }

            val config = CreateConfig.Builder("long_url")
                .deeplinks(deeplinks)
                .domain("domain")
                .groupGuid("group_guid")
                .tags(arrayOf("tag", "tag2"))
                .title("title")
                .build()

            assertThat(config).all {
                prop(CreateConfig::deeplinks).isEqualTo(deeplinks)
                prop(CreateConfig::domain).isEqualTo("domain")
                prop(CreateConfig::group_guid).isEqualTo("group_guid")
                prop(CreateConfig::long_url).isEqualTo("long_url")
                prop(CreateConfig::tags).isEqualTo(arrayOf("tag", "tag2"))
                prop(CreateConfig::title).isEqualTo("title")
                prop(CreateConfig::toJson).isEqualTo(false)
            }

            val map = mapOf(
                "deeplinks" to arrayOf(deeplinks.links()),
                "domain" to config.domain,
                "group_guid" to config.group_guid,
                "long_url" to config.long_url,
                "tags" to config.tags,
                "title" to config.title
            )

            assertThat(JSONObject(map).toString()).isEqualTo(
                """
                {"group_guid":"group_guid","deeplinks":[{"app_id":"app_id","install_type":"auto_install"}],"long_url":"long_url","title":"title","domain":"domain","tags":["tag","tag2"]}
            """.trimIndent()
            )
        }
    }

    @Test
    fun `Build update configuration`() {
        val deeplinks = UpdateDeeplinks().apply {
            os(Os.IOS)
            install_type(InstallType.PROMOTE_INSTALL)
            app_guid("app_guid")
        }

        val config = UpdateConfig.Builder("blink")
            .archived(true)
            .deeplinks(deeplinks)
            .tags(arrayOf("tag", "tag2"))
            .title("title")
            .build()

        assertThat(config).all {
            prop(UpdateConfig::archived).isTrue()
            prop(UpdateConfig::bitlink).isEqualTo("blink")
            prop(UpdateConfig::deeplinks).isEqualTo(deeplinks)
            prop(UpdateConfig::tags).isEqualTo(arrayOf("tag", "tag2"))
            prop(UpdateConfig::title).isEqualTo("title")
            prop(UpdateConfig::toJson).isEqualTo(false)
        }

        val map = mapOf(
            "archived" to config.archived,
            "bitlink" to config.bitlink,
            "deeplinks" to arrayOf(deeplinks.links()),
            "tags" to config.tags,
            "title" to config.title
        )

        assertThat(JSONObject(map).toString()).isEqualTo(
            """
                {"archived":true,"bitlink":"blink","deeplinks":[{"os":"ios","app_guid":"app_guid","install_type":"promote_install"}],"title":"title","tags":["tag","tag2"]}
            """.trimIndent()
        )
    }

    @Nested
    @DisplayName("Validate Configuration Tests")
    inner class ValidateConfigurationTests {
        @Test
        fun `Validate create configuration`() {
            val deeplinks = CreateDeeplinks().apply {
                app_id("app_id")
                install_type(InstallType.AUTO_INSTALL)
            }

            val config = CreateConfig.Builder("long_url")
                .deeplinks(deeplinks)
                .domain("domain")
                .groupGuid("group_guid")
                .tags(arrayOf("tag", "tag2"))
                .title("title")
                .toJson(true)

            assertThat(config).all {
                prop(CreateConfig.Builder::deeplinks).prop(CreateDeeplinks::links).isEqualTo(deeplinks.links())
                prop(CreateConfig.Builder::domain).isEqualTo("domain")
                prop(CreateConfig.Builder::group_guid).isEqualTo("group_guid")
                prop(CreateConfig.Builder::long_url).isEqualTo("long_url")
                prop(CreateConfig.Builder::tags).isEqualTo(arrayOf("tag", "tag2"))
                prop(CreateConfig.Builder::title).isEqualTo("title")
                prop(CreateConfig.Builder::toJson).isTrue()
            }

            config.longUrl("longer_url")
            assertThat(config).prop(CreateConfig.Builder::long_url).isEqualTo("longer_url")
        }

        @Test
        fun `Validate create default configuration`() {
            val config = CreateConfig.Builder("long_url")

            assertThat(config).all {
                prop(CreateConfig.Builder::long_url).isEqualTo("long_url")
                prop(CreateConfig.Builder::domain).isEqualTo(Constants.EMPTY)
                prop(CreateConfig.Builder::group_guid).isEqualTo(Constants.EMPTY)
                prop(CreateConfig.Builder::title).isEqualTo(Constants.EMPTY)
                prop(CreateConfig.Builder::tags).isEqualTo(emptyArray())
                prop(CreateConfig.Builder::deeplinks).prop(CreateDeeplinks::links).isEqualTo(CreateDeeplinks().links())
                prop(CreateConfig.Builder::toJson).isEqualTo(false)
            }
        }

        @Test
        fun `Validate update configuration`() {
            val deeplinks = UpdateDeeplinks().apply {
                os(Os.IOS)
                install_type(InstallType.PROMOTE_INSTALL)
                app_guid("app_guid")
            }

            val config = UpdateConfig.Builder("bitlink")
                .title("title")
                .archived(true)
                .tags(arrayOf("tag", "tag2"))
                .deeplinks(deeplinks)
                .toJson(true)

            assertThat(config).all {
                prop(UpdateConfig.Builder::bitlink).isEqualTo("bitlink")
                prop(UpdateConfig.Builder::title).isEqualTo("title")
                prop(UpdateConfig.Builder::archived).isTrue()
                prop(UpdateConfig.Builder::tags).isEqualTo(arrayOf("tag", "tag2"))
                prop(UpdateConfig.Builder::deeplinks).isEqualTo(deeplinks)
                prop(UpdateConfig.Builder::toJson).isTrue()
            }

            config.bitlink("blink")
            assertThat(config).prop(UpdateConfig.Builder::bitlink).isEqualTo("blink")
        }

        @Test
        fun `Validate update default configuration`() {
            val config = UpdateConfig.Builder("bitlink")

            assertThat(config).all {
                prop(UpdateConfig.Builder::bitlink).isEqualTo("bitlink")
                prop(UpdateConfig.Builder::title).isEqualTo(Constants.EMPTY)
                prop(UpdateConfig.Builder::archived).isEqualTo(false)
                prop(UpdateConfig.Builder::tags).isEqualTo(emptyArray())
                prop(UpdateConfig.Builder::deeplinks).prop(UpdateDeeplinks::links).isEqualTo(UpdateDeeplinks().links())
                prop(UpdateConfig.Builder::toJson).isEqualTo(false)
            }
        }
    }
}
