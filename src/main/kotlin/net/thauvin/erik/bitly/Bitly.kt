package net.thauvin.erik.bitly

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The HTTP methods.
 */
enum class Methods {
    DELETE, GET, PATCH, POST
}

/**
 * A simple implementation of the Bitly API v4.
 *
 * @constructor Creates new instance.
 */
open class Bitly() {
    /** Constants for this package. **/
    object Constants {
        /** The Bitly API base URL. **/
        const val API_BASE_URL = "https://api-ssl.bitly.com/v4"

        /** The API access token environment variable. **/
        const val ENV_ACCESS_TOKEN = "BITLY_ACCESS_TOKEN"
    }

    /** The API access token. **/
    var accessToken: String = System.getenv(Constants.ENV_ACCESS_TOKEN) ?: ""

    /** The logger instance. **/
    val logger: Logger by lazy { Logger.getLogger(Bitly::class.java.simpleName) }

    private var client: OkHttpClient

    init {
        if (logger.isLoggable(Level.FINE)) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
            }
            client = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
        } else {
            client = OkHttpClient.Builder().build()
        }
    }

    /**
     * Creates a new instance using an [API Access Token][accessToken].
     *
     * @param accessToken The API access token.
     */
    @Suppress("unused")
    constructor(accessToken: String) : this() {
        this.accessToken = accessToken
    }

    /**
     * Creates a new instance using a [Properties][properties] and [Property Key][key].
     *
     * @param properties The properties.
     * @param key The property key.
     */
    @Suppress("unused")
    @JvmOverloads
    constructor(properties: Properties, key: String = Constants.ENV_ACCESS_TOKEN) : this() {
        accessToken = properties.getProperty(key, accessToken)
    }

    /**
     * Creates a new instance using a [Properties File Path][propertiesFilePath] and [Property Key][key].
     *
     * @param propertiesFilePath The properties file path.
     * @param key The property key.
     */
    @JvmOverloads
    constructor(propertiesFilePath: Path, key: String = Constants.ENV_ACCESS_TOKEN) : this() {
        if (Files.exists(propertiesFilePath)) {
            accessToken = Properties().apply {
                Files.newInputStream(propertiesFilePath).use { nis ->
                    load(nis)
                }
            }.getProperty(key, accessToken)
        }
    }

    /**
     * Creates a new instance using a [Properties File][propertiesFile] and [Property Key][key].
     *
     * @param propertiesFile The properties file.
     * @param key The property key.
     */
    @Suppress("unused")
    @JvmOverloads
    constructor(propertiesFile: File, key: String = Constants.ENV_ACCESS_TOKEN) : this(propertiesFile.toPath(), key)

    /**
     * Builds the full API endpoint URL using the [Constants.API_BASE_URL].
     *
     * @param endPointPath The REST method path. (eg. `/shorten', '/user`)
     */
    fun buildEndPointUrl(endPointPath: String): String {
        return if (endPointPath.startsWith('/')) {
            "${Constants.API_BASE_URL}$endPointPath"
        } else {
            "${Constants.API_BASE_URL}/$endPointPath"
        }
    }

    /**
     * Executes an API call.
     *
     * @param endPoint The API endpoint. (eg. `/shorten`, `/user`)
     * @param params The request parameters kev/value map.
     * @param method The submission [Method][Methods].
     * @return The response (JSON) from the API.
     */
    fun executeCall(endPoint: String, params: Map<String, String>, method: Methods = Methods.POST): String {
        var returnValue = ""
        if (endPoint.isBlank()) {
            logger.severe("Please specify a valid API endpoint.")
        } else if (accessToken.isBlank()) {
            logger.severe("Please specify a valid API access token.")
        } else {
            val apiUrl = endPoint.toHttpUrlOrNull()
            val builder: Request.Builder
            if (apiUrl != null) {
                if (method == Methods.POST || method == Methods.PATCH) {
                    val formBody = JSONObject(params).toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    builder = Request.Builder().apply {
                        url(apiUrl.newBuilder().build())
                        if (method == Methods.POST)
                            post(formBody)
                        else
                            patch(formBody)
                    }
                } else if (method == Methods.DELETE) {
                    builder = Request.Builder().url(apiUrl.newBuilder().build()).delete()
                } else {
                    val httpUrl = apiUrl.newBuilder().apply {
                        params.forEach {
                            addQueryParameter(it.key, it.value)
                        }
                    }.build()
                    builder = Request.Builder().url(httpUrl)
                }
                builder.addHeader("Authorization", "Bearer $accessToken")

                val result = client.newCall(builder.build()).execute()

                val body = result.body?.string()
                if (body != null) {
                    if (!result.isSuccessful && body.isNotEmpty()) {
                        logApiError(body, result.code)
                    }
                    returnValue = body
                }
            }
        }

        return returnValue
    }

    /**
     * Shortens a long URL.
     *
     * See the [Bit.ly API](https://dev.bitly.com/v4/#operation/createBitlink) for more information.
     *
     * @param long_url The long URL.
     * @param group_guid The group UID.
     * @param domain The domain, defaults to `bit.ly`.
     * @param isJson Returns the full JSON API response if `true`
     * @return THe short URL or JSON API response.
     */
    @JvmOverloads
    fun shorten(long_url: String, group_guid: String = "", domain: String = "", isJson: Boolean = false): String {
        var returnValue = if (isJson) "{}" else ""
        if (!validateUrl(long_url)) {
            logger.severe("Please specify a valid URL to shorten.")
        } else {
            val params: HashMap<String, String> = HashMap()
            if (group_guid.isNotBlank())
                params["group_guid"] = group_guid
            if (domain.isNotBlank())
                params["domain"] = domain
            params["long_url"] = long_url

            val response = executeCall(buildEndPointUrl("/shorten"), params)

            if (response.isNotEmpty()) {
                if (isJson) {
                    returnValue = response
                } else {
                    try {
                        val json = JSONObject(response)
                        if (json.has("link"))
                            returnValue = json.getString("link")
                    } catch (ignore: JSONException) {
                        logger.severe("An error occurred parsing the response from bitly.")
                    }
                }
            }
        }

        return returnValue
    }

    private fun logApiError(body: String, resultCode: Int) {
        try {
            val jsonResponse = JSONObject(body)
            if (jsonResponse.has("message")) {
                logger.severe(jsonResponse.getString("message") + " ($resultCode)")
            }
            if (jsonResponse.has("description")) {
                val description = jsonResponse.getString("description")
                if (description.isNotBlank()) {
                    logger.severe(description)
                }
            }
        } catch (ignore: JSONException) {
            logger.severe("An error occurred parsing the error response from bitly.")
        }
    }

    private fun validateUrl(url: String): Boolean {
        var isValid = url.isNotBlank()
        if (isValid) {
            try {
                URL(url)
            } catch (e: MalformedURLException) {
                logger.log(Level.FINE, "Invalid URL: $url", e)
                isValid = false
            }
        }
        return isValid
    }
}
