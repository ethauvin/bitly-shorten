package net.thauvin.erik.bitly

import org.junit.Before
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.util.logging.Level
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun getKey(key: String): String {
    var value = System.getenv(key) ?: ""
    if (value.isBlank()) {
        val localProps = File("local.properties")
        if (localProps.exists())
            localProps.apply {
                if (exists()) {
                    FileInputStream(this).use { fis ->
                        Properties().apply {
                            load(fis)
                            value = getProperty(key, "")
                        }
                    }
                }
            }
    }
    return value
}

class BitlyTest {
    private val bitly = Bitly(getKey(Bitly.Constants.ENV_ACCESS_TOKEN))

    @Before
    fun before() {
        with(bitly.logger) {
            level = Level.FINE
        }
    }

    @Test
    fun `token should be specified`() {
        val test = Bitly()
        assertEquals("", test.shorten("https://erik.thauvin.net/blog/"))
    }

    @Test
    fun `token should be valid`() {
        val test = Bitly().apply { accessToken = "12345679" }
        assertEquals("{\"message\":\"FORBIDDEN\"}", test.shorten("https://erik.thauvin.net/blog", isJson = true))
    }

    @Test
    fun `long url should be valid`() {
        assertEquals("", bitly.shorten(""))
    }

    @Test
    fun `blog should be valid`() {
        assertEquals("http://bit.ly/2SVHsnd", bitly.shorten("https://erik.thauvin.net/blog/", domain = "bit.ly"))
    }

    @Test
    fun `blog as json`() {
        assertTrue(bitly.shorten("https://erik.thauvin.net/blog/", isJson = true).startsWith("{\"created_at\":"))
    }

    @Test
    fun `get user`() {
        assertTrue(bitly.executeCall(bitly.buildEndPointUrl("user"), emptyMap(), Methods.GET).contains("\"login\":"))
    }
}
