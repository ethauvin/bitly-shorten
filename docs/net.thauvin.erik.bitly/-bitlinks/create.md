[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Bitlinks](index.md) / [create](./create.md)

# create

`@JvmOverloads fun create(domain: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, title: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, group_guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, tags: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = emptyArray(), deeplinks: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>> = emptyArray(), long_url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Bitlinks.kt#L99)

Converts a long url to a Bitlink and sets additional parameters.

See the [Bit.ly API](https://dev.bitly.com/v4/#operation/createFullBitlink) for more information.

### Parameters

`toJson` - Returns the full JSON response if `true`

**Oaran**
long_url The long URL.

**Return**
The shorten URL or JSON response, or on error, an empty string/JSON object.

