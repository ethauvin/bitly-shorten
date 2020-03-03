[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Utils](./index.md)

# Utils

`open class Utils` [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Utils.kt#L50)

Useful functions.

### Companion Object Properties

| Name | Summary |
|---|---|
| [logger](logger.md) | The logger instance.`val logger: `[`Logger`](https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [call](call.md) | Executes an API call.`fun call(accessToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, endPoint: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, params: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> = emptyMap(), method: `[`Methods`](../-methods/index.md)` = Methods.POST): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [isValidUrl](is-valid-url.md) | Validates a URL.`fun `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`.isValidUrl(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeHttp](remove-http.md) | Removes http(s) scheme from string.`fun `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`.removeHttp(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [toEndPoint](to-end-point.md) | Builds the full API endpoint URL using the [Constants.API_BASE_URL](../-constants/-a-p-i_-b-a-s-e_-u-r-l.md).`fun `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`.toEndPoint(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
