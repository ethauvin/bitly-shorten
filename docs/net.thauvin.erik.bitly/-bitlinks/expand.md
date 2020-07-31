[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Bitlinks](index.md) / [expand](./expand.md)

# expand

`@Synchronized @JvmOverloads fun expand(bitlink_id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Bitlinks.kt#L146)

Expands a Bitlink.

See the [Bit.ly API](https://dev.bitly.com/v4/#operation/expandBitlink) for more information.

### Parameters

`bitlink_id` - The bitlink ID.

`toJson` - Returns the full JSON response if `true`

**Return**
The long URL or JSON response, or on error, an empty string/JSON object.

