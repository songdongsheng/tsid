# TSID
A general-purpose trend sorted identifier generator.

## Snowflake
Snowflake is an identifier algorithm which can generate unique numbers at high scale with some simple guarantees.

The name borrowed from [Snowflake](https://github.com/twitter/snowflake/releases/tag/snowflake-2010).

This is the modified version of the original snowflake algorithm.
If clocks that run backwards or sequence ID run out of space,
do not refuse to generate new id, use the next ms instead.

Snowflake identifier can be long integer (1 bit 0 + 41 bit ms + 10 bit instanceId + 12 bit sequenceId),
or encoded as 13 Crockford's base32 chars (42 bit ms + 10 bit instanceId + 12 bit sequenceId).

## ULID
Universally Unique Lexicographically Sortable Identifier ported to Java.

Original idea borrowed from [JS](https://github.com/alizain/ulid).

**Why another wheel ? Because this implementation use
[ThreadLocal](http://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html)
to gain a huge speed up in the multi-thread environment !**

ULID is 128-bit (48 bit ms + 80 bit entropy), encoded as 26 Crockford's base32 chars.

## TSID80
This is the shorten version of ULID.

TSID80 is 80-bit (48 bit ms + 32 bit entropy), encoded as 16 Crockford's base32 chars.

## TSID
This is the variant version of ULID.

TSID is 100-bit (58 bit us + 42 bit entropy), encoded as 20 Crockford's base32 chars.

## TSID120
This is the longer variant version of ULID.

TSID120 is 120-bit (58 bit us + 62 bit entropy), encoded as 24 Crockford's base32 chars.

# Maven dependency
You can also integrate TSID with your project by adding it as a dependency. TSID is hosted on maven central.
Here's the maven snippet to include in your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.songdongsheng</groupId>
    <artifactId>identifier</artifactId>
    <version>2.3.0</version>
</dependency>
```

# Performance
## 1 thread (I7-6500U)

|Benchmark              |Mode|Cnt|    Score    |    Error     |Units|
|:----------------------|:-:|---:|------------:|-------------:|----:|
|nextSnowflakeIdLong    |thrpt|10| 37889809.393| ± 3818256.904|ops/s|
|nextSnowflakeId        |thrpt|10| 20895418.716| ±  296770.537|ops/s|
|nextSnowflakeIdBase16  |thrpt|10| 17728656.802| ±  119036.211|ops/s|
|nextTSID               |thrpt|10|  6198648.392| ±   69267.953|ops/s|
|nextULID               |thrpt|10|  4215873.830| ±  115930.154|ops/s|
|nextThreadedUUID       |thrpt|10|  2405839.744| ±    4128.545|ops/s|
|nextJavaUUID           |thrpt|10|  2307956.152| ±  106999.002|ops/s|

## 2 threads (I7-6500U)
|Benchmark              |Mode|Cnt|    Score    |    Error    |Units|
|:----------------------|:-:|---:|------------:|------------:|----:|
|nextSnowflakeId        |thrpt|10| 26874794.317| ± 410898.123|ops/s|
|nextSnowflakeIdLong    |thrpt|10| 26108995.211| ± 312445.844|ops/s|
|nextSnowflakeIdBase16  |thrpt|10| 23175405.109| ± 382143.361|ops/s|
|nextTSID               |thrpt|10|  9832013.382| ± 286570.176|ops/s|
|nextULID               |thrpt|10|  6535540.741| ± 335516.750|ops/s|
|nextThreadedUUID       |thrpt|10|  3631016.865| ± 114069.848|ops/s|
|nextJavaUUID           |thrpt|10|  1738730.072| ±  78055.197|ops/s|

## 4 threads (I7-6500U)
|Benchmark              |Mode|Cnt|    Score    |    Error    |Units|
|:----------------------|:-:|---:|------------:|------------:|----:|
|nextSnowflakeIdLong    |thrpt|10| 20683751.933| ± 158519.062|ops/s|
|nextSnowflakeId        |thrpt|10| 14524354.037| ±1041060.201|ops/s|
|nextTSID               |thrpt|10| 13584637.435| ±  93641.496|ops/s|
|nextSnowflakeIdBase16  |thrpt|10| 12247658.228| ± 250014.781|ops/s|
|nextULID               |thrpt|10|  9168675.125| ± 588524.947|ops/s|
|nextThreadedUUID       |thrpt|10|  5185603.248| ±  73332.258|ops/s|
|nextJavaUUID           |thrpt|10|  1566631.354| ±   3403.591|ops/s|
