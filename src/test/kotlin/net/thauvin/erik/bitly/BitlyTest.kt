/*
 * BitlyTest.kt
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

package net.thauvin.erik.bitly

import DisableOnCi
import assertk.all
import assertk.assertThat
import assertk.assertions.*
import net.thauvin.erik.bitly.Utils.isValidUrl
import net.thauvin.erik.bitly.Utils.removeHttp
import net.thauvin.erik.bitly.Utils.toEndPoint
import net.thauvin.erik.bitly.config.CreateConfig
import net.thauvin.erik.bitly.config.UpdateConfig
import net.thauvin.erik.bitly.config.deeplinks.CreateDeeplinks
import net.thauvin.erik.bitly.config.deeplinks.UpdateDeeplinks
import net.thauvin.erik.bitly.config.deeplinks.enums.InstallType
import net.thauvin.erik.bitly.config.deeplinks.enums.Os
import org.json.JSONObject
import org.junit.jupiter.api.BeforeAll
import java.io.File
import java.util.logging.Level
import kotlin.test.*

class BitlyTest {
    private val bitly = with(File("local.properties")) {
        if (exists()) {
            Bitly(toPath())
        } else {
            Bitly()
        }
    }
    private val longUrl = "https://erik.thauvin.net/blog"
    private val shortUrl = "https://bit.ly/380ojFd"

    @Test
    fun `token should be specified`() {
        val test = Bitly()
        if (System.getenv("CI") == "true") {
            test.accessToken = Constants.EMPTY
        }
        assertFailsWith(IllegalArgumentException::class) {
            test.bitlinks().shorten(longUrl)
        }
        assertFailsWith(IllegalArgumentException::class, "Utils.call()") {
            Utils.call("", "foo")
        }
    }

    @Test
    fun `token should be valid`() {
        val test = Bitly().apply { accessToken = "12345679" }
        assertEquals(
            "{\"message\":\"FORBIDDEN\"}",
            test.bitlinks().shorten("https://erik.thauvin.net/blog", toJson = true)
        )
    }

    @Test
    fun `long url should be valid`() {
        assertEquals(Constants.EMPTY, bitly.bitlinks().shorten(Constants.EMPTY))
    }

    @Test
    fun `long url should not be short`() {
        assertEquals(shortUrl, bitly.bitlinks().shorten(shortUrl))
    }

    @Test
    fun `endPoint should be specified`() {
        assertFailsWith(IllegalArgumentException::class, "bitly.call()") {
            bitly.call("")
        }
        assertFailsWith(IllegalArgumentException::class, "Utils.call()") {
            Utils.call("1234568", "")
        }
    }

    @Test
    fun `endPoint conversion`() {
        assertThat(Constants.API_BASE_URL.toEndPoint()).isEqualTo(Constants.API_BASE_URL)
        assertThat("path".toEndPoint()).isEqualTo("${Constants.API_BASE_URL}/path")
        assertThat("/path".toEndPoint()).isEqualTo("${Constants.API_BASE_URL}/path")
    }

    @Test
    fun `shorten = expand`() {
        val shortUrl = bitly.bitlinks().shorten(longUrl, domain = "bit.ly")
        assertEquals(longUrl, bitly.bitlinks().expand(shortUrl))
    }

    @Test
    fun `shorten as json`() {
        assertTrue(bitly.bitlinks().shorten(longUrl, toJson = true).startsWith("{\"created_at\":"))
    }

    @Test
    fun `get user`() {
        assertThat(bitly.call("user", method = Methods.GET), "call(user)")
            .prop(CallResponse::isSuccessful).isTrue()
        assertThat(Utils.call(bitly.accessToken, "user".toEndPoint(), method = Methods.GET), "call(/user)").all {
            prop(CallResponse::isSuccessful).isTrue()
            prop(CallResponse::body).contains("login")
        }
    }

    @Test
    fun `created by`() {
        assertEquals(
            "ethauvin",
            JSONObject(
                bitly.call(
                    "/bitlinks/${shortUrl.removeHttp()}",
                    method = Methods.GET
                ).body
            ).getString("created_by")
        )
    }

    @Test
    fun `bitlinks shorten`() {
        assertEquals(shortUrl, Bitlinks(bitly.accessToken).shorten(longUrl, domain = "bit.ly"))
    }

    @Test
    fun `bitlinks expand`() {
        assertEquals(longUrl, Bitlinks(bitly.accessToken).expand(shortUrl))
    }

    @Test
    fun `bitlinks lastCallResponse`() {
        val bl = Bitlinks(bitly.accessToken)
        bl.shorten(longUrl, domain = "bit.ly")
        assertThat(bl.lastCallResponse, "shorten(longUrl)").all {
            prop(CallResponse::isSuccessful).isTrue()
            prop(CallResponse::statusCode).isEqualTo(200)
            prop(CallResponse::body).contains("\"link\":\"$shortUrl\"")
            prop(CallResponse::message).isEmpty()
        }

        bl.shorten(shortUrl)
        assertThat(bl.lastCallResponse, "shorten(shortUrl)").all {
            prop(CallResponse::isSuccessful).isFalse()
            prop(CallResponse::statusCode).isEqualTo(400)
            prop(CallResponse::isBadRequest).isTrue()
            prop(CallResponse::message).isEqualTo("ALREADY_A_BITLY_LINK")
            prop(CallResponse::description).isEqualTo("The value provided is invalid.")
        }
    }

    @Test
    fun `clicks summary`() {
        val bl = bitly.bitlinks()
        assertThat(bl.clicks(shortUrl)).isNotEqualTo(Constants.EMPTY)
        bl.clicks(shortUrl, unit = Units.MONTH, units = 6)
        assertThat(bl.lastCallResponse).all {
            prop(CallResponse::isUpgradeRequired)
            prop(CallResponse::statusCode).isEqualTo(402)
            prop(CallResponse::description).startsWith("Metrics")
        }
    }

    @Test
    fun `create bitlink`() {
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
    fun `create bitlink with config`() {
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
    fun `create bitlink with deeplinks`() {
        val bl = bitly.bitlinks()
        val dl = CreateDeeplinks().apply {
            install_type(InstallType.NO_INSTALL)
            app_uri_path("/store?id=123456")
            install_url("https://play.google.com/store/apps/details?id=com.bitly.app&hl=en_US")
        }

        val config = CreateConfig.Builder(longUrl)
            .domain("bit.ly")
            .deeplinks(dl)
            .build()

        assertThat(bl.create(config)).isEqualTo(Constants.EMPTY)
        assertThat(bl.lastCallResponse.isUpgradeRequired).isTrue()
    }

    @Test
    fun `shorten with invalid domain`() {
        val bl = bitly.bitlinks()
        bl.shorten("https://www.examples.com", domain = "foo.com")
        assertThat(bl.lastCallResponse).all {
            prop(CallResponse::isSuccessful).isFalse()
            prop(CallResponse::isBadRequest).isTrue()
            prop(CallResponse::message).isEqualTo("INVALID_ARG_DOMAIN")
            prop(CallResponse::description).contains("invalid")
        }
    }

    @Test
    @DisableOnCi
    fun `update custom bitlink`() {
        val bl = bitly.bitlinks()
        assertEquals(
            Constants.TRUE,
            bl.updateCustom("https://thauv.in/2NwtljT", "thauv.in/2NwtljT")
        )
    }

    @Test
    fun `update bitlink`() {
        val bl = bitly.bitlinks()
        assertEquals(
            Constants.TRUE,
            bl.update(shortUrl, title = "Erik's Weblog", tags = arrayOf("blog", "weblog"), archived = true)
        )

        assertThat(bl.update(shortUrl, tags = emptyArray(), toJson = true), "update(tags)")
            .contains("\"tags\":[]")
    }

    @Test
    fun `update bitlink with config`() {
        val bl = bitly.bitlinks()
        var config = UpdateConfig.Builder(shortUrl)
            .title("Erik's Weblog")
            .tags(arrayOf("blog", "weblog"))
            .archived(true)
            .build()

        assertEquals(Constants.TRUE, bl.update(config))

        config = UpdateConfig.Builder(shortUrl)
            .toJson(true)
            .build()

        assertThat(bl.update(config), "update(tags)").contains("\"tags\":[]")
    }

    @Test
    fun `update bitlink with deeplinks`() {
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
    fun `validate URL`() {
        assertTrue("https://www.example.com".isValidUrl(), "valid url")
        assertFalse("this is a test".isValidUrl(), "invalid url")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            with(Utils.logger) {
                level = Level.FINE
            }
        }
    }
}
