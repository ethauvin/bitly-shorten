[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Bitlinks](./index.md)

# Bitlinks

`open class Bitlinks` [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Bitlinks.kt#L47)

Provides functions to create and manage [Bitlinks](https://dev.bitly.com/v4/#tag/Bitlinks).

See the [Bitly API](https://dev.bitly.com/v4/#tag/Bitlinks) for more information.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Provides functions to create and manage [Bitlinks](https://dev.bitly.com/v4/#tag/Bitlinks).`Bitlinks(accessToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Functions

| Name | Summary |
|---|---|
| [clicks](clicks.md) | Returns the click counts for a specified Bitlink.`fun clicks(bitlink: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, unit: `[`Units`](../-units/index.md)` = Units.DAY, units: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = -1, size: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 50, unit_reference: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [create](create.md) | Converts a long url to a Bitlink and sets additional parameters.`fun create(domain: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, title: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, group_guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, tags: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = emptyArray(), deeplinks: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>> = emptyArray(), long_url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [expand](expand.md) | Expands a Bitlink.`fun expand(bitlink_id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [shorten](shorten.md) | Shortens a long URL.`fun shorten(long_url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, group_guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, domain: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
