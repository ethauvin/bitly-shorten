[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](https://opensource.org/licenses/BSD-3-Clause)
[![Kotlin](https://img.shields.io/badge/kotlin-2.3.0-7f52ff)](https://kotlinlang.org/)
[![bld](https://img.shields.io/badge/2.3.0-FA9052?label=bld&labelColor=2392FF)](https://rife2.com/bld)
[![Release](https://img.shields.io/github/release/ethauvin/bitly-shorten.svg)](https://github.com/ethauvin/bitly-shorten/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/net.thauvin.erik/bitly-shorten.svg?color=blue)](https://central.sonatype.com/artifact/net.thauvin.erik/bitly-shorten)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fnet%2Fthauvin%2Ferik%2Fbitly-shorten%2Fmaven-metadata.xml&label=snapshot)](https://github.com/ethauvin/bitly-shorten/packages/2260734/versions)


[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_bitly-shorten&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_bitly-shorten)
[![GitHub CI](https://github.com/ethauvin/bitly-shorten/actions/workflows/bld.yml/badge.svg)](https://github.com/ethauvin/bitly-shorten/actions/workflows/bld.yml)
[![CircleCI](https://circleci.com/gh/ethauvin/bitly-shorten/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/bitly-shorten/tree/master)

# [Bitly](https://dev.bitly.com/v4/) Shortener for Kotlin, Java & Android

A simple implementation of the [Bitly](https://bit.ly/) link shortening (Bitlinks) [API v4](https://dev.bitly.com/api-reference).

## Examples (TL;DR)

```kotlin
val bitly = Bitly(/* "YOUR_API_ACCESS_TOKEN from https://bitly.is/accesstoken" */)

// Shorten
bitly.bitlinks().shorten("https://erik.thauvin.net/blog")

// Expand
bitly.bitlinks().expand("https://bit.ly/380ojFd")

// Clicks Summary
bitly.bitlinks().clicks("https://bit.ly/380ojFd")

// Create a bitlink
bitly.bitlinks().create(title="Erik's Weblog", long_url = "https://erik.thauvin.net/blog/")

// Update a bitlink
bitly.bitlinks().update("https://bit.ly/380ojFd", title = "Erik's Weblog", tags = arrayOf("blog", "weblog"))
```

- View [bld](https://github.com/ethauvin/bitly-shorten/blob/master/examples/bld) or [Gradle](https://github.com/ethauvin/bitly-shorten/blob/master/examples/gradle) Examples.

## API Access Token

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

## bld

To use with [bld](https://rife2.com/bld), include the following dependency in your [build](https://github.com/ethauvin/bitly-shorten/blob/master/examples/bld/src/bld/java/com/example/ExampleBuild.java) file:

```java
repositories = List.of(MAVEN_CENTRAL, CENTRAL_SNAPSHOTS);

scope(compile)
    .include(dependency("net.thauvin.erik:bitly-shorten:2.0.0"));
```

Be sure to use the [bld Kotlin extension](https://github.com/rife2/bld-kotlin) in your project.

## Gradle, Maven, etc…

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/bitly-shorten/blob/master/examples/gradle/build.gradle.kts) file:

```gradle
repositories {
    maven {
        name = 'Central Portal Snapshots'
        url = 'https://central.sonatype.com/repository/maven-snapshots/'
    }
    mavenCentral()
}

dependencies {
    implementation("net.thauvin.erik:bitly-shorten:2.0.0")
}
```

Instructions for using with Maven, Ivy, etc. can be found on [Maven Central](https://central.sonatype.com/artifact/net.thauvin.erik/bitly-shorten).

## Java

To make it easier to use the library with Java, configuration builders are available:

```java
var config = new CreateConfig.Builder("https://erik.thauvin.net/blog")
        .title("Erik's Weblog")
        .tags(new String[] { "blog", "weblog"})
        .build();

bitly.bitlinks().create(config);
```

```java
var config = new UpdateConfig.Builder("https://bit.ly/380ojFd")
        .title("Erik's Weblog")
        .tags(new String[] { "blog", "weblog"})
        .build();

bitly.bitlinks().update(config);
```

## JSON

All implemented API calls can return the full JSON responses:

```kotlin
bitly.bitlinks().shorten("https://www.erik.thauvin.net/blog", toJson = true)
```

```json
{
    "created_at": "2020-02-26T06:50:08+0000",
    "link": "https://bit.ly/380ojFd",
    "id": "bit.ly/380ojFd",
    "long_url": "https://erik.thauvin.net/blog"
}
```

## API Response & Endpoints

You can also access the last response from implemented API calls using:

```kotlin
val bitlinks = Bitlinks(apikey)
val shortUrl = bitlinks.shorten(longUrl)
val response = bitlinks.lastCallResponse

if (response.isSuccessful) {
    println(response.body)
} else {
    println("${response.message}: ${response.description} (${response.statusCode})")
}
```

Non-implemented API endpoints can also be called directly:

```kotlin
val response = bitly.call("/user", method = Methods.GET)
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

- View [Example](https://github.com/ethauvin/bitly-shorten/blob/master/examples/bld/src/main/kotlin/com/example/BitlyRetrieve.kt)

## Contributing

See [CONTIBUTING.md](https://github.com/ethauvin/bitly-shorten?tab=contributing-ov-file#readme) for information about
contributing to this project.

## More…

If all else fails, there's always more [Documentation](https://ethauvin.github.io/bitly-shorten/).
