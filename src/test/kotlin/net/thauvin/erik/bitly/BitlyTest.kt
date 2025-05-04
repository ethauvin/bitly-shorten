/*
 * BitlyTest.kt
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

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isTrue
import assertk.assertions.prop
import net.thauvin.erik.bitly.Utils.removeHttp
import net.thauvin.erik.bitly.Utils.toEndPoint
import org.json.JSONObject
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import java.io.File
import java.util.logging.Level
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BitlyTest {
    private val bitly = with(File("local.properties")) {
        if (exists()) {
            Bitly(toPath())
        } else {
            Bitly()
        }
    }
    private val shortUrl = "https://bit.ly/380ojFd"

    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            with(Utils.logger) {
                level = Level.FINE
            }
        }
    }


    @Nested
    @DisplayName("API Call Tests")
    inner class ApiCallTests {
        @Test
        fun `Created by`() {
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
        fun `EndPoint should be specified`() {
            assertFailsWith(IllegalArgumentException::class, "bitly.call()") {
                bitly.call("")
            }
            assertFailsWith(IllegalArgumentException::class, "Utils.call()") {
                Utils.call("1234568", "")
            }
        }

        @Test
        fun `Get user`() {
            assertThat(bitly.call("user", method = Methods.GET), "call(user)")
                .prop(CallResponse::isSuccessful).isTrue()
            assertThat(
                Utils.call(
                    bitly.accessToken, "user".toEndPoint(), method = Methods.GET
                ), "call(/user)"
            ).all {
                prop(CallResponse::isSuccessful).isTrue()
                prop(CallResponse::body).contains("login")
            }
        }
    }
}
