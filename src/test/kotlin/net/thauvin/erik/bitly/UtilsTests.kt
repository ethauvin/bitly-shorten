/*
 * UtilsTests.kt
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

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.thauvin.erik.bitly.Utils.isValidUrl
import net.thauvin.erik.bitly.Utils.removeHttp
import net.thauvin.erik.bitly.Utils.toEndPoint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTests {
    @Nested
    @DisplayName("Endpoint Conversion Tests")
    inner class EndpointConversionTests {
        @Test
        fun `Convert endpoint with empty string`() {
            assertThat("".toEndPoint()).isEqualTo("")
        }

        @Test
        fun `Convert endpoint with full URL`() {
            assertThat("https://example.com/path".toEndPoint()).isEqualTo("https://example.com/path")
        }

        @Test
        fun `Convert endpoint with leading slash`() {
            assertThat("/path".toEndPoint()).isEqualTo("${Constants.API_BASE_URL}/path")
        }

        @Test
        fun `Convert endpoint with multiple path segments`() {
            assertThat("/existing/path".toEndPoint()).isEqualTo("${Constants.API_BASE_URL}/existing/path")
        }

        @Test
        fun `Convert endpoint with no leading slash`() {
            assertThat("path".toEndPoint()).isEqualTo("${Constants.API_BASE_URL}/path")
        }

        @Test
        fun `Convert endpoint with trailing slash`() {
            assertThat("path/".toEndPoint()).isEqualTo("${Constants.API_BASE_URL}/path/")
        }
    }

    @Nested
    @DisplayName("Remote HTTP Tests")
    inner class RemoteHTTPTests {
        @Test
        @Suppress("HttpUrlsUsage")
        fun `Remove HTTP`() {
            assertThat("http://example.com".removeHttp()).isEqualTo("example.com")
        }

        @Test
        fun `Remove HTTPS`() {
            assertThat("https://example.com".removeHttp()).isEqualTo("example.com")
        }

        @Test
        fun `Remove mixed case`() {
            assertThat("HtTPs://EXAMPLE.Com".removeHttp()).isEqualTo("EXAMPLE.Com")
        }

        @Test
        fun `Remove no scheme`() {
            assertThat("example.com".removeHttp()).isEqualTo("example.com")
        }
    }

    @Test
    fun `Validate invalid URL`() {
        assertFalse("this is a test".isValidUrl(), "invalid url")
    }

    @Test
    fun `Validate URL`() {
        assertTrue("https://www.example.com".isValidUrl(), "valid url")
    }
}
