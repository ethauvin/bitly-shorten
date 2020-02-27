/*
 * BitlyTest.kt
 *
 * Copyright (c) 2020, Erik C. Thauvin (erik@thauvin.net)
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

import org.junit.Before
import java.io.File
import java.util.logging.Level
import kotlin.test.Test
import kotlin.test.assertEquals
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
    private val shortUrl = "http://bit.ly/380ojFd"

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
        assertEquals("{\"message\":\"FORBIDDEN\"}", test.bitlinks().shorten("https://erik.thauvin.net/blog", isJson = true))
    }

    @Test
    fun `long url should be valid`() {
        assertEquals(Constants.EMPTY, bitly.bitlinks().shorten(Constants.EMPTY))
    }

    @Test
    fun `shorten = expand`() {
        val shortUrl = bitly.bitlinks().shorten(longUrl, domain = "bit.ly")
        assertEquals(longUrl, bitly.bitlinks().expand(shortUrl))
    }

    @Test
    fun `as json`() {
        assertTrue(bitly.bitlinks().shorten(longUrl, isJson = true).startsWith("{\"created_at\":"))
    }

    @Test
    fun `get user`() {
        assertTrue(bitly.call(Utils.buildEndPointUrl("user"), emptyMap(), Methods.GET).contains("\"login\":"))
    }

    @Test
    fun `bitlinks shorten`() {
        assertEquals(shortUrl, Bitlinks(bitly.accessToken).shorten(longUrl, domain="bit.ly"))
    }

    @Test
    fun `bitlinks expand`() {
        assertEquals(longUrl, Bitlinks(bitly.accessToken).expand(shortUrl))
    }
}
