package com.github.songdongsheng.identifier;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * This is the shorten version of {@link ULID}.
 */
public final class TSID {
    private static final ThreadLocal<ThreadLocalHolder> threadLocalHolder =
            ThreadLocal.withInitial(ThreadLocalHolder::new);
    private static final char[] ENCODING_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
            'Y', 'Z',
    };

    private TSID() {
    }

    /**
     * Returns the next TSID (100 bit = 58 bit us + 42 bit entropy) in the Crockford's base32 format.
     *
     * @return The next TSID
     */
    public static String next() {
        Instant now = Instant.now(); // use UTC-SLS
        long ct = now.getEpochSecond() * 1000_000 + now.getNano() / 1000;

        ThreadLocalHolder threadLocal = threadLocalHolder.get();
        SecureRandom random = threadLocal.random;
        byte[] entropy = threadLocal.entropy;
        char[] ids = threadLocal.ids;

        if (ct <= threadLocal.lastTimestamp) {
            // byte[6]
            int nh = ((entropy[5] & 0xFF) << 8) | (entropy[4] & 0xFF);
            int nl = ((entropy[3] & 0xFF) << 24) |  ((entropy[2] & 0xFF) << 16) | ((entropy[1] & 0xFF) << 8) | (entropy[0] & 0xFF);
            long n = (((long) nh) << 32 | (nl & 0xFFFFFFFFL)) + 1;

            entropy[5] = (byte) (n >> 40);
            entropy[4] = (byte) (n >> 32);
            entropy[3] = (byte) (n >> 24);
            entropy[2] = (byte) (n >> 16);
            entropy[1] = (byte) (n >> 8);
            entropy[0] = (byte) (n);
        } else {
            threadLocal.lastTimestamp = ct;
            random.nextBytes(entropy);
        }

        ids[0] = ENCODING_CHARS[(int) (ct >> 53) & 0x1F];
        ids[1] = ENCODING_CHARS[(int) (ct >> 48) & 0x1F];
        ids[2] = ENCODING_CHARS[(int) (ct >> 43) & 0x1F];
        ids[3] = ENCODING_CHARS[(int) (ct >> 38) & 0x1F];
        ids[4] = ENCODING_CHARS[(int) (ct >> 33) & 0x1F];
        ids[5] = ENCODING_CHARS[(int) (ct >> 28) & 0x1F];
        ids[6] = ENCODING_CHARS[(int) (ct >> 23) & 0x1F];
        ids[7] = ENCODING_CHARS[(int) (ct >> 18) & 0x1F];
        ids[8] = ENCODING_CHARS[(int) (ct >> 13) & 0x1F];
        ids[9] = ENCODING_CHARS[(int) (ct >> 8) & 0x1F];
        ids[10] = ENCODING_CHARS[(int) (ct >> 3) & 0x1F];
        ids[11] = ENCODING_CHARS[((int) (ct) & 0x07) << 2 | ((entropy[0] >> 6) & 0x03)]; // 3 + 2 -> 50
        ids[12] = ENCODING_CHARS[(entropy[0] >> 1) & 0x1F]; // 5 + 0
        ids[13] = ENCODING_CHARS[((entropy[0] & 0x01) << 4) | ((entropy[1] >> 4) & 0x0F)]; // 1 + 4
        ids[14] = ENCODING_CHARS[((entropy[1] & 0x0F) << 1) | ((entropy[2] >> 7) & 0x01)]; // 4 + 1
        ids[15] = ENCODING_CHARS[(entropy[2] >> 2) & 0x1F]; // 5 + 0
        ids[16] = ENCODING_CHARS[((entropy[2] & 0x03) << 3) | (entropy[3] >> 5) & 0x07]; // 2 + 3
        ids[17] = ENCODING_CHARS[entropy[3] & 0x1F]; // 5 + 0
        ids[18] = ENCODING_CHARS[(entropy[4] >> 3) & 0x1F]; // 5 + 0
        ids[19] = ENCODING_CHARS[((entropy[4] & 0x07) << 2) | ((entropy[5] >> 6) & 0x03)]; // 3 + 2

        return new String(ids);
    }

    private static final class ThreadLocalHolder {
        long lastTimestamp;
        final byte[] entropy;
        final char[] ids;
        final SecureRandom random;

        ThreadLocalHolder() {
            entropy = new byte[6];
            ids = new char[20];
            SecureRandom t;
            try {
                t = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                t = new SecureRandom();
            }
            random = t;
        }
    }
}
