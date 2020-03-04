[docs](../../index.md) / [net.thauvin.erik.bitly](../index.md) / [CallResponse](./index.md)

# CallResponse

`data class CallResponse` [(source)](https://github.com/ethauvin/bitly-shorten/tree/master/src/main/kotlin/net/thauvin/erik/bitly/CallResponse.kt#L40)

Provides a data class to hold the JSON response.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Provides a data class to hold the JSON response.`CallResponse(body: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = Constants.EMPTY_JSON, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = -1)` |

### Properties

| Name | Summary |
|---|---|
| [body](body.md) | `var body: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [isSuccessful](is-successful.md) | `val isSuccessful: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [resultCode](result-code.md) | `var resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [toJson](to-json.md) | `fun toJson(): JSONObject` |
