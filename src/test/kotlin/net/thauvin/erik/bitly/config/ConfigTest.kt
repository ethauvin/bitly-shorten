/*
 * ConfigTest.kt
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

package net.thauvin.erik.bitly.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import net.thauvin.erik.bitly.deeplinks.CreateDeeplinks
import net.thauvin.erik.bitly.deeplinks.InstallType
import net.thauvin.erik.bitly.deeplinks.Os
import net.thauvin.erik.bitly.deeplinks.UpdateDeeplinks
import org.json.JSONObject
import kotlin.test.Test

class ConfigTest {
    @Test
    fun `create config test`() {
        val deeplinks = CreateDeeplinks().apply {
            app_id("app_id")
            install_type(InstallType.AUTO_INSTALL)
        }

        val config = CreateConfig(
            "long_url",
            "domain",
            "group_guid",
            "title",
            arrayOf("tag", "tag2"),
            deeplinks,
        )

        val map = mapOf(
            "long_url" to config.long_url,
            "domain" to config.domain,
            "group_guid" to config.group_guid,
            "title" to config.title,
            "tags" to config.tags,
            "deeplinks" to arrayOf(deeplinks.links())
        )

        assertThat(JSONObject(map).toString()).isEqualTo(
            """
                {"group_guid":"group_guid","long_url":"long_url","title":"title","deeplinks":[{"app_id":"app_id","install_type":"auto_install"}],"domain":"domain","tags":["tag","tag2"]}
            """.trimIndent()
        )
    }

    @Test
    fun `update config test`() {
        val deeplinks = UpdateDeeplinks().apply {
            os(Os.IOS)
            install_type(InstallType.PROMOTE_INSTALL)
            app_guid("app_guid")
        }

        val config = UpdateConfig(
            "blink",
            "title",
            true,
            arrayOf("tag", "tag2"),
            deeplinks
        )
        val map = mapOf(
            "bitlink" to config.bitlink,
            "title" to config.title,
            "archived" to config.archived,
            "tags" to config.tags,
            "deeplinks" to arrayOf(deeplinks.links())
        )

        assertThat(JSONObject(map).toString()).isEqualTo(
            """
                {"archived":true,"bitlink":"blink","title":"title","deeplinks":[{"os":"ios","app_guid":"app_guid","install_type":"promote_install"}],"tags":["tag","tag2"]}
            """.trimIndent()
        )

    }
}
