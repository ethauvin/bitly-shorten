/*
 * UtilsTests.kt
 *
 * Copyright 2020-2026 Erik C. Thauvin (erik@thauvin.net)
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

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import net.thauvin.erik.bitly.Utils.isValidUrl
import net.thauvin.erik.bitly.Utils.removeHttp
import net.thauvin.erik.bitly.Utils.toEndPoint
import org.json.JSONObject
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTests {
    private lateinit var server: MockWebServer

    @BeforeEach
    fun beforeEach() {
        server = MockWebServer()
        server.start()
    }

    @AfterEach
    fun afterEach() {
        server.close()
    }

    @Nested
    @DisplayName("API call() Function Tests")
    inner class CallFunctionTests {
        private val accessToken = "TEST_ACCESS_TOKEN"

        @Test
        fun `Call with blank endpoint throws IllegalArgumentException`() {
            val exception = assertThrows<IllegalArgumentException> {
                Utils.call(accessToken = accessToken, endPoint = " ")
            }
            assertThat(exception.message).isEqualTo("A valid API endpoint must be specified.")
        }

        @Test
        fun `Call with blank access token throws IllegalArgumentException`() {
            val exception = assertThrows<IllegalArgumentException> {
                Utils.call(accessToken = " ", endPoint = "https://example.com")
            }
            assertThat(exception.message).isEqualTo("A valid API access token must be provided.")
        }

        @Test
        fun `Call with GET method builds correct URL and headers`() {
            server.enqueue(MockResponse(code = 200, body = "{}"))
            val endpoint = server.url("/v4/user").toString()
            val params = mapOf("login" to "testuser", "page" to "1")

            Utils.call(accessToken, endpoint, params, Methods.GET)

            val request = server.takeRequest()
            assertAll {
                assertThat(request.method).isEqualTo("GET")
                assertTrue(request.url.toString().endsWith("/v4/user?login=testuser&page=1"))
                assertThat(request.headers["Authorization"]).isEqualTo("Bearer $accessToken")
            }
        }

        @Test
        fun `Call with POST method sends correct JSON body`() {
            server.enqueue(MockResponse(code = 200, body = "{}"))
            val endpoint = server.url("/v4/shorten").toString()
            val params = mapOf("long_url" to "https://example.com", "domain" to "bit.ly")

            Utils.call(accessToken, endpoint, params, Methods.POST)

            val request = server.takeRequest()

            assertNotNull(request.body)
            val requestBody = JSONObject(request.body!!.string(Charsets.UTF_8))
            assertAll {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.headers["Content-Type"]).isEqualTo("application/json; charset=utf-8")
                assertThat(requestBody.getString("long_url")).isEqualTo("https://example.com")
                assertThat(requestBody.getString("domain")).isEqualTo("bit.ly")
            }
        }

        @Test
        fun `Call with PATCH method sends correct JSON body`() {
            server.enqueue(MockResponse(code = 200, body = "{}"))
            val endpoint = server.url("/v4/bitlinks/bit.ly/abc").toString()
            val params = mapOf("title" to "New Title")

            Utils.call(accessToken, endpoint, params, Methods.PATCH)

            val request = server.takeRequest()

            assertNotNull(request.body)
            val requestBody = JSONObject(request.body!!.string(Charsets.UTF_8))
            assertAll {
                assertThat(request.method).isEqualTo("PATCH")
                assertThat(request.headers["Content-Type"]).isEqualTo("application/json; charset=utf-8")
                assertThat(requestBody.getString("title")).isEqualTo("New Title")
            }
        }

        @Test
        fun `Call with DELETE method sends correct request`() {
            server.enqueue(MockResponse(code = 204))
            val endpoint = server.url("/v4/groups/some-guid").toString()

            Utils.call(accessToken, endpoint, method = Methods.DELETE)

            val request = server.takeRequest()
            assertAll {
                assertThat(request.method).isEqualTo("DELETE")
                assertNotNull(request.body)
                assertThat(request.body!!.size).isEqualTo(0)
            }
        }

        @Test
        fun `Call returns successfully parsed response on success`() {
            val responseBody = """{"link": "http://bit.ly/xyz", "id": "bit.ly/xyz"}"""
            server.enqueue(MockResponse(code = 200, body = responseBody))
            val endpoint = server.url("/v4/shorten").toString()

            val response = Utils.call(accessToken, endpoint)

            assertAll {
                assertThat(response.statusCode).isEqualTo(200)
                assertThat(response.body).isEqualTo(responseBody)
                assertThat(response.message).isEqualTo("OK") // Default message from OkHttp
                assertThat(response.description).isEqualTo("")
            }
        }

        @Test
        fun `Call returns parsed error response on failure with JSON body`() {
            val errorBody = """{"message": "FORBIDDEN", "description": "You do not have access to this resource."}"""
            server.enqueue(
                MockResponse.Builder()
                    .code(403)
                    .body(errorBody)
                    .status("HTTP/1.1 403 Forbidden")
                    .build()
            )
            val endpoint = server.url("/v4/shorten").toString()

            val response = Utils.call(accessToken, endpoint)

            assertAll {
                assertThat(response.statusCode).isEqualTo(403)
                assertThat(response.body).isEqualTo(errorBody)
                assertThat(response.message).isEqualTo("FORBIDDEN")
                assertThat(response.description).isEqualTo("You do not have access to this resource.")
            }
        }

        @Test
        fun `Call returns generic error on failure with non-JSON body`() {
            val errorBody = "Internal Server Error"
            server.enqueue(
                MockResponse.Builder()
                    .code(500)
                    .body(errorBody)
                    .status("HTTP/1.1 500 Server Error")
                    .build()
            )
            val endpoint = server.url("/v4/shorten").toString()

            val response = Utils.call(accessToken, endpoint)

            assertAll {
                assertThat(response.statusCode).isEqualTo(500)
                assertThat(response.body).isEqualTo(errorBody)
                assertThat(response.message).isEqualTo("Server Error") // From status line
                assertThat(response.description).isEqualTo("")
            }
        }
    }

    @Nested
    @DisplayName("Endpoint Conversion Tests")
    inner class EndpointConversionTests {
        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun `Convert endpoint with empty or blank strings`(input: String) {
            assertThat(input.toEndPoint()).isEqualTo(input)
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
    @DisplayName("Remove HTTP Tests")
    inner class RemoveHTTPTests {
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

    @Nested
    @DisplayName("URL Validation Tests")
    inner class URLValidationTests {
        @Test
        fun `Validate invalid URL`() {
            assertFalse("this is a test".isValidUrl(), "invalid url")
        }

        @Test
        fun `Validate URL`() {
            assertTrue("https://www.example.com".isValidUrl(), "valid url")
        }

        @Test
        fun `Validate domain only URL`() {
            assertTrue("example.com".isValidUrl(), "valid url")
        }
    }
}
