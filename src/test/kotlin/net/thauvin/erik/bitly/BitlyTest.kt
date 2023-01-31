/*
 * BitlyTest.kt
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

package net.thauvin.erik.bitly

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.matches
import assertk.assertions.prop
import net.thauvin.erik.bitly.Utils.Companion.isValidUrl
import net.thauvin.erik.bitly.Utils.Companion.removeHttp
import net.thauvin.erik.bitly.Utils.Companion.toEndPoint
import org.json.JSONObject
import org.junit.Before
import java.io.File
import java.util.logging.Level
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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

    @Before
    fun before() {
        with(Utils.logger) {
            level = Level.FINE
        }
    }

    @Test
    fun `token should be specified`() {
        val test = Bitly()
        if (System.getenv("CI") == "true") {
            test.accessToken = Constants.EMPTY
        }
        assertEquals(longUrl, test.bitlinks().shorten(longUrl))
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
        assertThat(bitly.call("")).prop(CallResponse::isSuccessful).isFalse()
    }

    @Test
    fun `shorten = expand`() {
        val shortUrl = bitly.bitlinks().shorten(longUrl, domain = "bit.ly")
        assertEquals(longUrl, bitly.bitlinks().expand(shortUrl))
    }

    @Test
    fun `as json`() {
        assertTrue(bitly.bitlinks().shorten(longUrl, toJson = true).startsWith("{\"created_at\":"))
    }

    @Test
    fun `get user`() {
        assertThat(bitly.call("user".toEndPoint(), method = Methods.GET), "call(user)")
            .prop(CallResponse::isSuccessful).isTrue()
        assertThat(Utils.call(bitly.accessToken, "/user".toEndPoint(), method = Methods.GET), "call(/user)")
            .prop(CallResponse::isSuccessful).isTrue()
    }

    @Test
    fun `created by`() {
        assertEquals(
            "ethauvin",
            JSONObject(
                bitly.call(
                    "/bitlinks/${shortUrl.removeHttp()}".toEndPoint(),
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
            prop(CallResponse::resultCode).isEqualTo(200)
            prop(CallResponse::body).contains("\"link\":\"$shortUrl\"")
        }

        bl.shorten(shortUrl)
        assertThat(bl.lastCallResponse, "shorten(shortUrl)").all {
            prop(CallResponse::isSuccessful).isFalse()
            prop(CallResponse::resultCode).isEqualTo(400)
            prop(CallResponse::isBadRequest).isTrue()
            prop(CallResponse::body).contains("ALREADY_A_BITLY_LINK")
        }
    }

    @Test
    fun `clicks summary`() {
        assertNotEquals(Constants.EMPTY, bitly.bitlinks().clicks(shortUrl))
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
        var config = CreateConfig.Builder().apply {
            long_url = longUrl
        }.build()
        assertThat(bitly.bitlinks().create(config), "create(config)")
            .matches("https://\\w+.\\w{2}/\\w{7}".toRegex())

        config = CreateConfig.Builder().apply {
            domain = "bit.ly"
            title = "Erik's Blog"
            tags = arrayOf("erik", "thauvin", "blog", "weblog")
            long_url = longUrl
        }.build()
        assertEquals(
            shortUrl,
            bitly.bitlinks().create(config)
        )
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
    fun `update bitlink`() {
        val bl = bitly.bitlinks()
        assertEquals(
            Constants.TRUE,
            bl.update(shortUrl, title = "Erik's Weblog", tags = arrayOf("blog", "weblog"), archived = true)
        )

        assertThat(bl.update(shortUrl, tags = emptyArray(), toJson = true), "update(tags)")
            .contains("\"tags\":[]")

        bl.update(shortUrl, link = longUrl)
        assertThat(bl.lastCallResponse).prop(CallResponse::isUnprocessableEntity).isTrue()

        bl.update("bit.ly/407GjJU", id = "foo")
        assertThat(bl.lastCallResponse).all {
            prop(CallResponse::isForbidden).isTrue()
            prop(CallResponse::resultCode).isEqualTo(403)
        }
    }

    @Test
    fun `update bitlink with config`() {
        val bl = bitly.bitlinks()
        var config = UpdateConfig.Builder().apply {
            bitlink(shortUrl)
            title("Erik's Weblog")
            tags(arrayOf("blog", "weblog"))
            archived(true)
        }.build()

        assertEquals(Constants.TRUE, bl.update(config))

        config = UpdateConfig.Builder().apply {
            bitlink(shortUrl)
            toJson(true)
        }.build()

        assertThat(bl.update(config), "update(tags)").contains("\"tags\":[]")

        config = UpdateConfig.Builder().apply {
            bitlink(shortUrl)
            link(longUrl)
        }.build()
        bl.update(config)

        assertThat(bl.lastCallResponse).all {
            prop(CallResponse::isUnprocessableEntity).isTrue()
            prop(CallResponse::resultCode).isEqualTo(422)
        }
    }

    @Test
    fun `validate URL`() {
        assertTrue("https://www.example.com".isValidUrl(), "valid url")
        assertFalse("this is a test".isValidUrl(), "invalid url")
    }
}
