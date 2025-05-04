/*
 * DeeplinksTest.kt
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

package net.thauvin.erik.bitly.config.deeplinks

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.prop
import net.thauvin.erik.bitly.config.deeplinks.enums.InstallType
import net.thauvin.erik.bitly.config.deeplinks.enums.Os
import org.json.JSONObject
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class DeeplinksTest {
    @Test
    fun `Create deeplink`() {
        val deeplinks = CreateDeeplinks().apply {
            app_uri_path("app_uri_path")
            install_type(InstallType.NO_INSTALL)
        }

        assertThat(deeplinks).all {
            prop(CreateDeeplinks::app_id).isNull()
            prop(CreateDeeplinks::app_uri_path).isEqualTo("app_uri_path")
            prop(CreateDeeplinks::install_type).isEqualTo(InstallType.NO_INSTALL)
            prop(CreateDeeplinks::install_url).isNull()
            prop(CreateDeeplinks::links).isEqualTo(deeplinks.links())
        }

        assertThat(JSONObject(deeplinks.links()).toString()).isEqualTo(
            """
                {"app_uri_path":"app_uri_path","install_type":"no_install"}
            """.trimIndent()
        )

        deeplinks.app_id("app_id")
        deeplinks.install_type(InstallType.PROMOTE_INSTALL)
        deeplinks.install_url("install_url")

        assertThat(deeplinks).all {
            prop(CreateDeeplinks::app_id).isEqualTo("app_id")
            prop(CreateDeeplinks::install_type).isEqualTo(InstallType.PROMOTE_INSTALL)
            prop(CreateDeeplinks::install_url).isEqualTo("install_url")
        }

        assertThat(JSONObject(deeplinks.links()).toString()).isEqualTo(
            """
                {"install_url":"install_url","app_id":"app_id","app_uri_path":"app_uri_path","install_type":"promote_install"}
            """.trimIndent()
        )
    }

    @Test
    fun `Update deeplink`() {
        val deeplinks = UpdateDeeplinks().apply {
            app_guid("app_guid")
            app_uri_path("app_uri_path")
            created("created")
            guid("guid")
            install_type(InstallType.NO_INSTALL)
            install_url("install_url")
            modified("modified")
            os(Os.IOS)
        }

        assertThat(deeplinks).all {
            prop(UpdateDeeplinks::app_guid).isEqualTo("app_guid")
            prop(UpdateDeeplinks::app_uri_path).isEqualTo("app_uri_path")
            prop(UpdateDeeplinks::bitlink).isNull()
            prop(UpdateDeeplinks::brand_guid).isNull()
            prop(UpdateDeeplinks::created).isEqualTo("created")
            prop(UpdateDeeplinks::guid).isEqualTo("guid")
            prop(UpdateDeeplinks::install_type).isEqualTo(InstallType.NO_INSTALL)
            prop(UpdateDeeplinks::install_url).isEqualTo("install_url")
            prop(UpdateDeeplinks::links).isEqualTo(deeplinks.links())
            prop(UpdateDeeplinks::modified).isEqualTo("modified")
            prop(UpdateDeeplinks::os).isEqualTo(Os.IOS)
        }

        val zdt = ZonedDateTime.of(1997, 8, 29, 2, 14, 0, 0, ZoneId.of("US/Eastern"))

        deeplinks.bitlink("bitlink")
        deeplinks.brand_guid("brand_guid")
        deeplinks.created(zdt)
        deeplinks.install_type(InstallType.PROMOTE_INSTALL)
        deeplinks.modified(zdt)
        deeplinks.os(Os.ANDROID)

        assertThat(deeplinks).all {
            prop(UpdateDeeplinks::bitlink).isEqualTo("bitlink")
            prop(UpdateDeeplinks::brand_guid).isEqualTo("brand_guid")
            prop(UpdateDeeplinks::created).isEqualTo("1997-08-29T02:14:00-0400")
            prop(UpdateDeeplinks::install_type).isEqualTo(InstallType.PROMOTE_INSTALL)
            prop(UpdateDeeplinks::modified).isEqualTo("1997-08-29T02:14:00-0400")
            prop(UpdateDeeplinks::os).isEqualTo(Os.ANDROID)
        }

        assertThat(JSONObject(deeplinks.links()).toString()).isEqualTo(
            """
                {"app_guid":"app_guid","install_url":"install_url","bitlink":"bitlink","os":"android","app_uri_path":"app_uri_path","created":"1997-08-29T02:14:00-0400","brand_guid":"brand_guid","guid":"guid","modified":"1997-08-29T02:14:00-0400","install_type":"promote_install"}
            """.trimIndent()
        )
    }
}
