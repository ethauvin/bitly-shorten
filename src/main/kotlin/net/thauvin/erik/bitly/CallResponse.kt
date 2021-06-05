/*
 * Response.kt
 *
 * Copyright (c) 2020-2021, Erik C. Thauvin (erik@thauvin.net)
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

/**
 * Provides a data class to hold the JSON response.
 */
@Suppress("unused")
data class CallResponse(val body: String = Constants.EMPTY_JSON, val resultCode: Int = -1) {
    val isSuccessful: Boolean
        get() = resultCode in 200..299
    val isCreated: Boolean
        get() = resultCode == 201
    val isBadRequest: Boolean
        get() = resultCode == 400
    val isUpgradeRequired: Boolean
        get() = resultCode == 402
    val isForbidden: Boolean
        get() = resultCode == 403
    val isNotFound: Boolean
        get() = resultCode == 404
    val isExpectationFailed: Boolean
        get() = resultCode == 417
    val isUnprocessableEntity: Boolean
        get() = resultCode == 422
    val isInternalError: Boolean
        get() = resultCode == 500
    val isTemporarilyUnavailable: Boolean
        get() = resultCode == 503
}
