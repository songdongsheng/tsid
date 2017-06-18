# TSID
A general-purpose trend sorted identifier generator.

## Snowflake ID
Snowflake is a network service for generating unique ID numbers at high scale with some simple guarantees.

Original idea borrowed from [Snowflake](https://github.com/twitter/snowflake/releases/tag/snowflake-2010)

This is the modified version of the original snowflake algorithm.
If clocks that run backwards or sequence ID run out of space,
do not refuse to generate new id, use the next ms instead.

## ULID
Universally Unique Lexicographically Sortable Identifier ported to Java.

Original idea borrowed from [JS](https://github.com/alizain/ulid)
Why another wheel ? Because this implementation use ThreadLocal to speed up in the multi-thread environment !

# Performance
## 1 thread

|Benchmark              |Mode|Cnt|    Score    |    Error     |Units|
|:----------------------|:-:|---:|------------:|-------------:|----:|
|nextSnowflakeId        |thrpt|10| 32526759.846| ± 7446396.465|ops/s|
|nextSnowflakeIdBase32  |thrpt|10| 17283116.454| ± 5779823.186|ops/s|
|nextSnowflakeIdBase16  |thrpt|10| 12464730.154| ± 4296232.934|ops/s|
|nextSortedUniqueId     |thrpt|10|  5525566.691| ±  929724.579|ops/s|
|nextULID               |thrpt|10|  3057188.593| ± 1311610.996|ops/s|
|nextJavaUUID           |thrpt|10|  2137815.039| ±  382973.452|ops/s|
|nextThreadedUUID       |thrpt|10|  1847581.931| ±  707251.161|ops/s|

## 2 threads
|Benchmark              |Mode|Cnt|    Score    |    Error    |Units|
|:----------------------|:-:|---:|------------:|------------:|----:|
|nextSnowflakeIdBase32  |thrpt|10| 29156261.451| ± 928597.367|ops/s|
|nextSnowflakeId        |thrpt|10| 26780872.759| ± 290810.751|ops/s|
|nextSnowflakeIdBase16  |thrpt|10| 20749481.458| ± 397822.980|ops/s|
|nextSortedUniqueId     |thrpt|10|  9065771.400| ± 369647.152|ops/s|
|nextULID               |thrpt|10|  6805954.426| ± 345279.340|ops/s|
|nextThreadedUUID       |thrpt|10|  3903993.339| ± 154745.819|ops/s|
|nextJavaUUID           |thrpt|10|  1776990.259| ±  36216.792|ops/s|
