[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Bitly](./index.md)

# Bitly

`open class Bitly` [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Bitly.kt#L45)

A simple implementation of the [Bitly Shortner API v4](https://dev.bitly.com/v4/).

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Creates a new instance using an [API Access Token](access-token.md).`Bitly(accessToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`<br>Creates a new instance using a [Properties](-init-.md#net.thauvin.erik.bitly.Bitly$<init>(java.util.Properties, kotlin.String)/properties) and [Property Key](-init-.md#net.thauvin.erik.bitly.Bitly$<init>(java.util.Properties, kotlin.String)/key).`Bitly(properties: `[`Properties`](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html)`, key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.ENV_ACCESS_TOKEN)`<br>Creates a new instance using a [Properties File Path](-init-.md#net.thauvin.erik.bitly.Bitly$<init>(java.nio.file.Path, kotlin.String)/propertiesFilePath) and [Property Key](-init-.md#net.thauvin.erik.bitly.Bitly$<init>(java.nio.file.Path, kotlin.String)/key).`Bitly(propertiesFilePath: `[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`, key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.ENV_ACCESS_TOKEN)`<br>Creates a new instance using a [Properties File](-init-.md#net.thauvin.erik.bitly.Bitly$<init>(java.io.File, kotlin.String)/propertiesFile) and [Property Key](-init-.md#net.thauvin.erik.bitly.Bitly$<init>(java.io.File, kotlin.String)/key).`Bitly(propertiesFile: `[`File`](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)`, key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.ENV_ACCESS_TOKEN)`<br>Creates new instance.`Bitly()` |

### Properties

| Name | Summary |
|---|---|
| [accessToken](access-token.md) | The API access token.`var accessToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [bitlinks](bitlinks.md) | Bitlinks accessor.`fun bitlinks(): `[`Bitlinks`](../-bitlinks/index.md) |
| [call](call.md) | Executes an API call.`fun call(endPoint: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, params: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = emptyMap(), method: `[`Methods`](../-methods/index.md)` = Methods.POST): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
