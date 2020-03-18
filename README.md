[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](http://opensource.org/licenses/BSD-3-Clause)  
[![Known Vulnerabilities](https://snyk.io/test/github/ethauvin/bitly-shorten/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/ethauvin/bitly-shorten?targetFile=pom.xml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_bitly-shorten&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_bitly-shorten) [![Build Status](https://travis-ci.org/ethauvin/bitly-shorten.svg?branch=master)](https://travis-ci.org/ethauvin/bitly-shorten) [![CircleCI](https://circleci.com/gh/ethauvin/bitly-shorten/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/bitly-shorten/tree/master)

# [Bitly](https://dev.bitly.com/v4/) Shortener for Kotlin/Java.

A simple implementation of the link shortening ([bitlinks](https://dev.bitly.com/v4/#tag/Bitlinks)) abilities of the [Bitly v4 API](https://dev.bitly.com/v4).

## Examples (TL;DR)

```kotlin
val bitly = Bitly(/* "YOUR_API_ACCESS_TOKEN from https://bitly.is/accesstoken" */)

// Shorten
bitly.bitlinks().shorten("https://erik.thauvin.net/blog")

// Expand
bitly.bitlinks().expand("http://bit.ly/380ojFd")

// Clicks Summary
bitly.bitlinks().clicks("http://bit.ly/380ojFd")  

// Create a bitlink
bitly.bitlinks().create(title = "Erik's Weblog", long_url = "http://erik.thauvin.net/blog/")               

// Update a bitlink
bitly.bitlinks().update("http://bit.ly/380ojFd", title="Erik's Weblog", tags = arrayOf("blog", "weblog"))
```

 - View [Kotlin](https://github.com/ethauvin/bitly-shorten/blob/master/examples/src/main/kotlin/com/example/BitlyExample.kt) or [Java](https://github.com/ethauvin/bitly-shorten/blob/master/examples/src/main/java/com/example/BitlySample.java) Examples.

### API Access Token

The Bitly API [Access Token](https://bitly.is/accesstoken) can be specified directly as well as via the `BITLY_ACCESS_TOKEN` environment variable or properties key.

```kotlin
// Env Variable or System Property
val bitly = Bitly()

// Properties file path
val bitly = Bitly(File("my.properties"))

```
```ini
# my.properties
BITLY_ACCESS_TOKEN=abc123def456ghi789jkl0
```

### JSON

All implemented methods can return the full API JSON responses:

```kotlin
bitly.bitlinks().shorten("https://www.erik.thauvin.net/blog", toJson = true)
```
```json
{
    "created_at": "2020-02-26T06:50:08+0000",
    "link": "http://bit.ly/380ojFd",
    "id": "bit.ly/380ojFd",
    "long_url": "https://erik.thauvin.net/blog"
}
```

Non-implemented methods can also be called directly:

```kotlin
val response = bitly.call("/user".toEndPoint(), method = Methods.GET)
if (response.isSuccessful) {
    println(response.body)
}
```
```json
{
    "created": "2009-06-12T19:00:45+0000",
    "modified": "2016-11-11T19:50:33+0000",
    "login": "johndoe",
    "is_active": true,
    "is_2fa_enabled": true,
    "name": "John Doe",
    "emails": [
        {
            "email": "john@doe.com",
            "is_primary": true,
            "is_verified": true
        }
    ],
    "is_sso_user": false,
    "default_group_guid": "ABCde1f23gh"
}
```
- View [Example](https://github.com/ethauvin/bitly-shorten/blob/master/examples/src/main/kotlin/com/example/BitlyRetrieve.kt)

### More...
If all else fails, there's always more [Documentation](https://ethauvin.github.io/bitly-shorten/).
