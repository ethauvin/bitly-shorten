/*
 * CallResponse.kt
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

/**
 * Provides a data class to hold the JSON response.
 *
 * @param body The response body.
 * @param message Bitly error message, if any.
 * @param description Bitly error description, if any.
 * @param statusCode HTTP status code,
 */
data class CallResponse(
    val body: String = Constants.EMPTY_JSON,
    val message: String = "",
    val description: String = "",
    val statusCode: Int = -1
) {
    val isSuccessful = statusCode in 200..299
    val isCreated = statusCode == 201
    val isBadRequest = statusCode == 400
    val isUpgradeRequired = statusCode == 402
    val isForbidden = statusCode == 403
    val isNotFound = statusCode == 404
    val isGone = statusCode == 410
    val isExpectationFailed = statusCode == 417
    val isUnprocessableEntity = statusCode == 422
    val isTooManyRequests = statusCode == 429
    val isInternalError = statusCode == 500
    val isTemporarilyUnavailable = statusCode == 503
}
