/*
 * Constants.kt
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

/** Provides the constants for this package. */
open class Constants private constructor() {
    companion object Constants {
        /**
         * The Bitly API base URL.
         *
         * @value `https://api-ssl.bitly.com/v4`
         */
        const val API_BASE_URL = "https://api-ssl.bitly.com/v4"

        /**
         * The API access token environment variable.
         *
         * @value `BITLY_ACCESS_TOKEN`
         */
        const val ENV_ACCESS_TOKEN = "BITLY_ACCESS_TOKEN"

        /** Empty String. */
        const val EMPTY = ""

        /** Empty JSON Object. */
        const val EMPTY_JSON = "{}"

        /**
         * False
         *
         * @value `false`
         */
        const val FALSE = false.toString()

        /**
         * True
         *
         * @value `true`
         */
        const val TRUE = true.toString()
    }
}
