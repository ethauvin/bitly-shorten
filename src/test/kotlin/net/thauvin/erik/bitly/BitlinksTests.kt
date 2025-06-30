/*
 * BitlinksTests.kt
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

package net.thauvin.erik.bitly

import DisableOnCi
import assertk.all
import assertk.assertThat
import assertk.assertions.*
import net.thauvin.erik.bitly.config.CreateConfig
import net.thauvin.erik.bitly.config.UpdateConfig
import net.thauvin.erik.bitly.config.deeplinks.CreateDeeplinks
import net.thauvin.erik.bitly.config.deeplinks.UpdateDeeplinks
import net.thauvin.erik.bitly.config.deeplinks.enums.InstallType
import net.thauvin.erik.bitly.config.deeplinks.enums.Os
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@ExtendWith(BeforeAllTests::class)
class BitlinksTests {
    private val bitly = with(File("local.properties")) {
        if (exists()) {
            Bitly(toPath())
        } else {
            Bitly()
        }
    }
    private val longUrl = "https://erik.thauvin.net/blog"
    private val shortUrl = "https://bit.ly/380ojFd"

    @Nested
    @DisplayName("Bitlinks Tests")
    inner class BitlinksTests {
        @Test
        fun `Clicks summary`() {
            val bl = bitly.bitlinks()
            val clicks = bl.clicks(shortUrl, unit = Units.MONTH, units = 1)
            assertThat(bl.lastCallResponse).all {
                prop(CallResponse::description).isEmpty()
                prop(CallResponse::isSuccessful).isTrue()
                prop(CallResponse::statusCode).isEqualTo(200)
            }
            assertThat(clicks.toInt()).isGreaterThanOrEqualTo(0)
        }

        @Test
        fun `Clicks summary as json`() {
            val bl = bitly.bitlinks()
            val clicks = bl.clicks(shortUrl, toJson = true)
            assertThat(bl.lastCallResponse).all {
                prop(CallResponse::description).isEmpty()
                prop(CallResponse::isSuccessful).isTrue()
                prop(CallResponse::statusCode).isEqualTo(200)
            }
            assertThat(clicks).startsWith("{\"unit_reference\":")
        }
    }

    @Nested
    @DisplayName("Create Bitlink Tests")
    inner class CreateBitlinkTests {
        @Test
        fun `Create bitlink`() {
            assertThat(bitly.bitlinks().create(long_url = longUrl), "create(longUrl)")
                .matches("https://\\w+.\\w{2}/\\w{7}".toRegex())
            assertEquals(
                shortUrl,
                bitly.bitlinks().create(
                    domain = "bit.ly",
                    title = "Erik's Blog",
                    tags = arrayOf("erik", "thauvin", "blog", "weblog"),
                    long_url = longUrl
                )
            )
        }

        @Test
        fun `Create bitlink with config`() {
            var config = CreateConfig.Builder(longUrl).build()
            assertThat(bitly.bitlinks().create(config), "create(config)")
                .matches("https://\\w+.\\w{2}/\\w{7}".toRegex())

            config = CreateConfig.Builder(longUrl)
                .domain("bit.ly")
                .title("Erik's Blog")
                .tags(arrayOf("erik", "thauvin", "blog", "weblog"))
                .build()
            assertEquals(
                shortUrl,
                bitly.bitlinks().create(config)
            )
        }

        @Test
        fun `Create bitlink with deep links`() {
            val bl = bitly.bitlinks()
            val dl = CreateDeeplinks().apply {
                app_uri_path("/store?id=123456")
                install_type(InstallType.NO_INSTALL)
                install_url("https://play.google.com/store/apps/details?id=com.bitly.app&hl=en_US")
            }

            val config = CreateConfig.Builder(longUrl)
                .deeplinks(dl)
                .domain("bit.ly")
                .build()

            assertThat(bl.create(config)).isEqualTo(Constants.EMPTY)
            assertThat(bl.lastCallResponse.isUpgradeRequired).isTrue()
        }
    }

    @Nested
    @DisplayName("Expand Test")
    inner class ExpandTests {
        @Test
        fun `Expand as json`() {
            assertTrue(
                bitly.bitlinks().expand(shortUrl, toJson = true)
                    .startsWith("{\"created_at\":")
            )
        }

        @Test
        fun `Expand link`() {
            assertEquals(longUrl, Bitlinks(bitly.accessToken).expand(shortUrl))
        }
    }

    @Nested
    @DisplayName("Shorten Tests")
    inner class ShortenTests {
        @Test
        fun `Shorten as json`() {
            assertTrue(
                bitly.bitlinks().shorten(longUrl, toJson = true)
                    .startsWith("{\"created_at\":")
            )
        }

        @Test
        fun `Shorten last call response`() {
            val bl = Bitlinks(bitly.accessToken)
            bl.shorten(longUrl, domain = "bit.ly")
            assertThat(bl.lastCallResponse, "shorten(longUrl)").all {
                prop(CallResponse::body).contains("\"link\":\"$shortUrl\"")
                prop(CallResponse::isSuccessful).isTrue()
                prop(CallResponse::message).isEmpty()
                prop(CallResponse::statusCode).isEqualTo(200)
            }

            bl.shorten(shortUrl)
            assertThat(bl.lastCallResponse, "shorten(shortUrl)").all {
                prop(CallResponse::description).isEqualTo("The value provided is invalid.")
                prop(CallResponse::isBadRequest).isTrue()
                prop(CallResponse::isSuccessful).isFalse()
                prop(CallResponse::message).isEqualTo("ALREADY_A_BITLY_LINK")
                prop(CallResponse::statusCode).isEqualTo(400)
            }
        }

        @Test
        fun `Shorten link`() {
            assertEquals(
                shortUrl, Bitlinks(bitly.accessToken)
                    .shorten(longUrl, domain = "bit.ly")
            )
        }

        @Test
        fun `Shorten with invalid domain`() {
            val bl = bitly.bitlinks()
            bl.shorten("https://www.examples.com", domain = "foo.com")
            assertThat(bl.lastCallResponse).all {
                prop(CallResponse::description).contains("invalid")
                prop(CallResponse::isBadRequest).isTrue()
                prop(CallResponse::isSuccessful).isFalse()
                prop(CallResponse::message).isEqualTo("INVALID_ARG_DOMAIN")
            }
        }
    }

    @Nested
    @DisplayName("Update Bitlink Tests")
    inner class UpdateBitlinkTests {
        @Test
        fun `Update bitlink`() {
            val bl = bitly.bitlinks()
            assertEquals(
                Constants.TRUE,
                bl.update(
                    shortUrl, title = "Erik's Weblog", tags = arrayOf("blog", "weblog"), archived = true
                )
            )

            assertThat(bl.update(shortUrl, tags = emptyArray(), toJson = true), "update(tags)")
                .contains("\"tags\":[]")
        }

        @Test
        fun `Update bitlink with config`() {
            val bl = bitly.bitlinks()
            var config = UpdateConfig.Builder(shortUrl)
                .archived(true)
                .tags(arrayOf("blog", "weblog"))
                .title("Erik's Weblog")
                .build()

            assertEquals(Constants.TRUE, bl.update(config))

            config = UpdateConfig.Builder(shortUrl)
                .toJson(true)
                .build()

            assertThat(bl.update(config), "update(tags)").contains("\"tags\":[]")
        }

        @Test
        fun `Update bitlink with deep links`() {
            val bl = bitly.bitlinks()
            val dl = UpdateDeeplinks().apply {
                os(Os.ANDROID)
                brand_guid("Ba1bc23dE4F")
            }
            val config = UpdateConfig.Builder(shortUrl)
                .deeplinks(dl)
                .build()

            assertThat(bl.update(config)).isEqualTo(Constants.FALSE)
            assertThat(bl.lastCallResponse.isUpgradeRequired).isTrue()
        }

        @Test
        @DisableOnCi
        fun `Update custom bitlink`() {
            val bl = bitly.bitlinks()
            assertEquals(
                Constants.TRUE,
                bl.updateCustom("https://thauv.in/2NwtljT", "thauv.in/2NwtljT")
            )
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    inner class ValidationTests {
        @Test
        fun `Empty URL should not shorten`() {
            assertEquals(Constants.EMPTY, bitly.bitlinks().shorten(Constants.EMPTY))
        }

        @Test
        fun `Short URL should not shorten`() {
            assertEquals(shortUrl, bitly.bitlinks().shorten(shortUrl))
        }

        @Test
        fun `Token not specified with API call`() {
            assertFailsWith(IllegalArgumentException::class, "Utils.call()") {
                Utils.call("", "foo")
            }
        }

        @Test
        @DisableOnCi
        fun `Token not specified`() {
            val test = Bitly()

            assertFailsWith(IllegalArgumentException::class) {
                test.bitlinks().shorten(longUrl)
            }
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
        fun `Token not specified on CI`() {
            val test = Bitly(Constants.EMPTY) // to avoid picking up the environment variable

            assertFailsWith(IllegalArgumentException::class) {
                test.bitlinks().shorten(longUrl)
            }
        }

        @Test
        fun `Token should be valid`() {
            val test = Bitly().apply { accessToken = "12345679" }
            assertEquals(
                "{\"message\":\"FORBIDDEN\"}",
                test.bitlinks().shorten("https://erik.thauvin.net/blog", toJson = true)
            )
        }
    }
}
