package com.github.songdongsheng.identifier;

/**
 * This is the modified version of the original snowflake algorithm.
 * <p>
 * If clocks that run backwards or sequence ID run out of space,
 * do not refuse to generate new id, use the next ms instead.
 */
public final class Snowflake {
    private static final char[] ENCODING_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
            'Y', 'Z',
    };
    private final ThreadLocal<ThreadLocalCache> threadCache =
            ThreadLocal.withInitial(ThreadLocalCache::new);
    private final int shiftId;
    private final Object idLock = new Object();
    private int sequenceId;
    private long lastEpochTime;

    /**
     * Constructs a snowflake identifier generator.
     *
     * @param id The instance ID.
     */
    public Snowflake(int id) {
        this.shiftId = (0x3FF & id) << 12;
    }

    private long nextLong() {
        long timeOffset = 1497571200000L; // date --date='TZ="UTC" 2017-06-16 00:00:00' +%s
        long ct = System.currentTimeMillis() - timeOffset;
        int cid = 0;
        synchronized (idLock) {
            if (lastEpochTime < ct) {
                lastEpochTime = ct;
                sequenceId = 0;
            } else {
                if (++sequenceId >= 4096) {
                    sequenceId = 0;
                    ct = ++lastEpochTime;
                } else {
                    cid = sequenceId;
                }
            }
        }

        return (ct << 22) | shiftId | (0xFFF & cid);
    }

    /**
     * Returns the next ID (1 bit 0 + 41 bit ms + 10 bit instanceId + 12 bit sequenceId).
     *
     * @return The next ID
     */
    public long nextId() {
        return nextLong() & Long.MAX_VALUE;
    }

    /**
     * Returns the next ID (42 bit ms + 10 bit instanceId + 12 bit sequenceId) in the Crockford's base32 format.
     *
     * @return The next ID
     */
    public String next() {
        ThreadLocalCache localCache = threadCache.get();
        char[] ids = localCache.ids;
        long id = nextLong();
        ids[0] = ENCODING_CHARS[(int) (id >> 59) & 0x1F];
        ids[1] = ENCODING_CHARS[(int) (id >> 54) & 0x1F];
        ids[2] = ENCODING_CHARS[(int) (id >> 49) & 0x1F];
        ids[3] = ENCODING_CHARS[(int) (id >> 44) & 0x1F];
        ids[4] = ENCODING_CHARS[(int) (id >> 39) & 0x1F];
        ids[5] = ENCODING_CHARS[(int) (id >> 34) & 0x1F];
        ids[6] = ENCODING_CHARS[(int) (id >> 29) & 0x1F];
        ids[7] = ENCODING_CHARS[(int) (id >> 24) & 0x1F];
        ids[8] = ENCODING_CHARS[(int) (id >> 19) & 0x1F];
        ids[9] = ENCODING_CHARS[(int) (id >> 14) & 0x1F];
        ids[10] = ENCODING_CHARS[(int) (id >> 9) & 0x1F];
        ids[11] = ENCODING_CHARS[(int) (id >> 4) & 0x1F];
        ids[12] = ENCODING_CHARS[(int) (id) & 0x0F];

        return new String(ids);
    }

    private static final class ThreadLocalCache {
        final char[] ids;

        ThreadLocalCache() {
            ids = new char[13];
        }
    }
}
