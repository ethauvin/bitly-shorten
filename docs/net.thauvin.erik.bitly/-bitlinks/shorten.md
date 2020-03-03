[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Bitlinks](index.md) / [shorten](./shorten.md)

# shorten

`@JvmOverloads fun shorten(long_url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, group_guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, domain: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Bitlinks.kt#L185)

Shortens a long URL.

See the [Bit.ly API](https://dev.bitly.com/v4/#operation/createBitlink) for more information.

### Parameters

`long_url` - The long URL.

`toJson` - Returns the full JSON response if `true`

**Return**
The short URL or JSON response, or on error, the [long_url](shorten.md#net.thauvin.erik.bitly.Bitlinks$shorten(kotlin.String, kotlin.String, kotlin.String, kotlin.Boolean)/long_url) or an empty JSON object.

