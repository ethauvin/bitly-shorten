[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [Bitlinks](index.md) / [clicks](./clicks.md)

# clicks

`@JvmOverloads fun clicks(bitlink: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, unit: `[`Units`](../-units/index.md)` = Units.DAY, units: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = -1, size: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 50, unit_reference: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY, toJson: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/Bitlinks.kt#L63)

Returns the click counts for a specified Bitlink.

See the [Bitly API](https://dev.bitly.com/v4/#operation/getClicksSummaryForBitlink) for more information.

### Parameters

`bitlink` - The bitlink.

`unit` - A unit of time.

`units` - An integer representing the time units to query data for. pass -1 to return all units available.

`size` - The quantity of items to be be returned.

`unit_reference` - An ISO-8601 timestamp, indicating the most recent time for which to pull metrics.
Will default to current time.

`toJson` - Returns the full JSON response if `true`

**Return**
The click counts or JSON response object.

