/*
 * BitlyTest.kt
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

import net.thauvin.erik.bitly.Utils.Companion.isValidUrl
import net.thauvin.erik.bitly.Utils.Companion.removeHttp
import net.thauvin.erik.bitly.Utils.Companion.toEndPoint
import org.json.JSONObject
import org.junit.Before
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
        assertFalse(bitly.call("").isSuccessful)
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
        assertTrue(bitly.call("user".toEndPoint(), method = Methods.GET).isSuccessful)
        assertTrue(
            Utils.call(bitly.accessToken, "/user".toEndPoint(), method = Methods.GET).isSuccessful,
            "call(/user)"
        )
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
        with(bl.lastCallResponse) {
            assertEquals(true, isSuccessful, "is successful")
            assertEquals(200, resultCode, "resultCode == 200")
            assertTrue(body.contains("\"link\":\"$shortUrl\""), "valid body")
        }

        bl.shorten(shortUrl)
        with(bl.lastCallResponse) {
            assertEquals(false, isSuccessful, "is not successful")
            assertEquals(400, resultCode, "resultCode == 400")
            assertTrue(isBadRequest, "is bad request")
            assertTrue(body.contains("ALREADY_A_BITLY_LINK"), "already a bitlink")
        }
    }

    @Test
    fun `clicks summary`() {
        assertNotEquals(Constants.EMPTY, bitly.bitlinks().clicks(shortUrl))
    }

    @Test
    fun `create bitlink`() {
        assertTrue(
            bitly.bitlinks().create(long_url = longUrl).matches("https://\\w+.\\w{2}/\\w{7}".toRegex()),
            "just long_url"
        )
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
    fun `update bitlink`() {
        val bl = bitly.bitlinks()
        assertEquals(
            Constants.TRUE,
            bl.update(shortUrl, title = "Erik's Weblog", tags = arrayOf("blog", "weblog"))
        )

        assertTrue(
            bl.update(shortUrl, tags = emptyArray(), toJson = true).contains("\"tags\":[]"), "update tags toJson"
        )

        bl.update(shortUrl, link = longUrl)
        assertTrue(bl.lastCallResponse.isUnprocessableEntity, "422 Unprocessable")
    }

    @Test
    fun `validate URL`() {
        assertTrue("https://www.example.com".isValidUrl(), "valid url")
        assertFalse("this is a test".isValidUrl(), "invalid url")
    }
}
