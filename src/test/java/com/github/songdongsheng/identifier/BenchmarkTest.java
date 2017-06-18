package com.github.songdongsheng.identifier;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Threads(2)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class BenchmarkTest {
    private final Snowflake snowflake = new Snowflake(1);

    public static void main(String[] args)
            throws RunnerException {
        Options options = new OptionsBuilder()
                .verbosity(VerboseMode.NORMAL)
                .include(".*" + BenchmarkTest.class.getSimpleName() + ".*")
                .build();

        new Runner(options).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public long nextSnowflakeId() {
        return snowflake.nextId();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String nextSnowflakeIdBase32() {
        return snowflake.next();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String nextSnowflakeIdBase16() {
        long id = snowflake.nextId();
        char[] buf = new char[16];
        for (int i = 0; i < 16; i++) {
            int index = (int) (id >>> (i << 2));
            if (index < 10) buf[i] = (char) ('0' + index);
            else buf[i] = (char) ('A' - 10 + index);
        }
        return new String(buf);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String nextSortedUniqueId() {
        return TrendSortedIdentifier.next();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String nextULID() {
        return ULID.next();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String nextJavaUUID() {
        UUID uuid = UUID.randomUUID();

        byte[] buf = new byte[16];
        long id = uuid.getMostSignificantBits();
        for (int i = 0; i < 8; i++) {
            buf[i] = (byte) (id >> (7 - i));
        }

        id = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            buf[i + 8] = (byte) (id >> (7 - i));
        }

        return new String(CrockfordBase32.encode(buf, new char[26]));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String nextThreadedUUID() {
        return RandomUUID.next();
    }
}
/*
1 thread:
Benchmark                             Mode  Cnt         Score         Error  Units
BenchmarkTest.nextSnowflakeId        thrpt   10  32526759.846 ± 7446396.465  ops/s
BenchmarkTest.nextSnowflakeIdBase32  thrpt   10  17283116.454 ± 5779823.186  ops/s
BenchmarkTest.nextSnowflakeIdBase16  thrpt   10  12464730.154 ± 4296232.934  ops/s
BenchmarkTest.nextSortedUniqueId     thrpt   10   5525566.691 ±  929724.579  ops/s
BenchmarkTest.nextULID               thrpt   10   3057188.593 ± 1311610.996  ops/s
BenchmarkTest.nextJavaUUID           thrpt   10   2137815.039 ±  382973.452  ops/s
BenchmarkTest.nextThreadedUUID       thrpt   10   1847581.931 ±  707251.161  ops/s

2 threads:
Benchmark                             Mode  Cnt         Score        Error  Units
BenchmarkTest.nextSnowflakeIdBase32  thrpt   10  29156261.451 ± 928597.367  ops/s
BenchmarkTest.nextSnowflakeId        thrpt   10  26780872.759 ± 290810.751  ops/s
BenchmarkTest.nextSnowflakeIdBase16  thrpt   10  20749481.458 ± 397822.980  ops/s
BenchmarkTest.nextSortedUniqueId     thrpt   10   9065771.400 ± 369647.152  ops/s
BenchmarkTest.nextULID               thrpt   10   6805954.426 ± 345279.340  ops/s
BenchmarkTest.nextThreadedUUID       thrpt   10   3903993.339 ± 154745.819  ops/s
BenchmarkTest.nextJavaUUID           thrpt   10   1776990.259 ±  36216.792  ops/s
 */
