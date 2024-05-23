/*
 * DeeplinksTest.kt
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

package net.thauvin.erik.bitly.config.deeplinks

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.json.JSONObject
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class DeeplinksTest {
    @Test
    fun `create test`() {
        val deeplinks = CreateDeeplinks().apply {
            app_uri_path("app_uri_path")
            install_type(InstallType.NO_INSTALL)
        }

        assertThat(deeplinks.install_url()).isNull()
        deeplinks.install_url("install_url")

        assertThat(deeplinks.app_uri_path()).isEqualTo("app_uri_path")
        assertThat(deeplinks.install_type()).isEqualTo(InstallType.NO_INSTALL)

        assertThat(JSONObject(deeplinks.links()).toString()).isEqualTo(
            """
                {"app_uri_path":"app_uri_path","install_type":"no_install","install_url":"install_url"}
            """.trimIndent()
        )

        deeplinks.install_type(InstallType.PROMOTE_INSTALL)
        deeplinks.app_id("app_id")

        assertThat(deeplinks.app_id()).isEqualTo("app_id")

        assertThat(JSONObject(deeplinks.links()).toString()).apply {
            doesNotContain(InstallType.NO_INSTALL.type)
            contains(InstallType.PROMOTE_INSTALL.type)
            contains("\"app_id\":\"app_id\"")
        }
    }

    @Test
    fun `update test`() {
        val deeplinks = UpdateDeeplinks().apply {
            app_guid("app_guid")
            os(Os.IOS)
            install_type(InstallType.NO_INSTALL)
            guid("guid")
            install_url("install_url")
            app_uri_path("app_uri_path")
            created("created")
            modified("modified")
        }

        assertThat(deeplinks.brand_guid()).isNull()
        deeplinks.brand_guid("brand_guid")

        assertThat(deeplinks.app_uri_path()).isEqualTo("app_uri_path")
        assertThat(deeplinks.install_url()).isEqualTo("install_url")

        assertThat(deeplinks.os()).isEqualTo(Os.IOS)
        assertThat(deeplinks.install_type()).isEqualTo(InstallType.NO_INSTALL)
        assertThat(deeplinks.app_guid()).isEqualTo("app_guid")
        assertThat(deeplinks.modified()).isEqualTo("modified")
        assertThat(deeplinks.brand_guid()).isEqualTo("brand_guid")


        assertThat(JSONObject(deeplinks.links()).toString()).isEqualTo(
            """
                {"app_guid":"app_guid","install_url":"install_url","os":"ios","app_uri_path":"app_uri_path","created":"created","brand_guid":"brand_guid","guid":"guid","modified":"modified","install_type":"no_install"}
            """.trimIndent()
        )

        deeplinks.install_type(InstallType.PROMOTE_INSTALL)
        deeplinks.os(Os.ANDROID)
        deeplinks.bitlink("bitlink")

        val zdt = ZonedDateTime.of(1997, 8, 29, 2, 14, 0, 0, ZoneId.of("US/Eastern"))
        deeplinks.modified(zdt)
        deeplinks.created(zdt)

        assertThat(deeplinks.bitlink()).isEqualTo("bitlink")
        assertThat(deeplinks.created()).isEqualTo("1997-08-29T02:14:00-0400")
        assertThat(deeplinks.modified()).isEqualTo("1997-08-29T02:14:00-0400")

        assertThat(JSONObject(deeplinks.links()).toString()).apply {
            doesNotContain(InstallType.NO_INSTALL.type)
            contains(InstallType.PROMOTE_INSTALL.type)

            doesNotContain(Os.IOS.type)
            contains("\"os\":\"android\"")

            contains("\"bitlink\":\"bitlink\"")
        }
    }
}
