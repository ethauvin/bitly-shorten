[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Bitly](index.md) / [call](./call.md)

# call

`@JvmOverloads fun call(endPoint: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, params: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> = emptyMap(), method: `[`Methods`](../-methods/index.md)` = Methods.POST): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Bitly.kt#L117)

Executes an API call.

### Parameters

`endPoint` - The REST endpoint. (eg. `https://api-ssl.bitly.com/v4/shorten`)

`params` - The request parameters key/value map.

`method` - The submission [Method](../-methods/index.md).

**Return**
The response (JSON) from the API.

