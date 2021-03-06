package com.github.songdongsheng.identifier;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Why another wheel ? Because this implementation use {@link ThreadLocal} to speed up in the multi-thread environment !
 * <p>
 * <p>See <a href="https://github.com/alizain/ulid"> <i>https://github.com/alizain/ulid</i></a> for more details.</p>
 */
public final class ULID {
    private static final ThreadLocal<ThreadLocalHolder> threadLocalHolder =
            ThreadLocal.withInitial(ThreadLocalHolder::new);
    private static final char[] ENCODING_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
            'Y', 'Z'
    };

    private ULID() {
    }

    /**
     * Returns the next ULID (128 bit = 48 bit ms + 80 bit entropy) in the Crockford's base32 format.
     *
     * @return The next ULID
     */
    public static String next() {
        long timeOffset = 1497571200000L; // date --date='TZ="UTC" 2017-06-16 00:00:00' +%s
        long ct = System.currentTimeMillis() - timeOffset;

        ThreadLocalHolder threadLocal = threadLocalHolder.get();
        SecureRandom random = threadLocal.random;
        byte[] entropy = threadLocal.entropy;
        char[] ids = threadLocal.ids;

        random.nextBytes(entropy);

        ids[0] = ENCODING_CHARS[(int) (ct >> 43) & 0x1F];
        ids[1] = ENCODING_CHARS[(int) (ct >> 38) & 0x1F];
        ids[2] = ENCODING_CHARS[(int) (ct >> 33) & 0x1F];
        ids[3] = ENCODING_CHARS[(int) (ct >> 28) & 0x1F];
        ids[4] = ENCODING_CHARS[(int) (ct >> 23) & 0x1F];
        ids[5] = ENCODING_CHARS[(int) (ct >> 18) & 0x1F];
        ids[6] = ENCODING_CHARS[(int) (ct >> 13) & 0x1F];
        ids[7] = ENCODING_CHARS[(int) (ct >> 8) & 0x1F];
        ids[8] = ENCODING_CHARS[(int) (ct >> 3) & 0x1F];
        ids[9] = ENCODING_CHARS[((int) (ct) & 0x07) << 2 | ((entropy[0] >> 6) & 0x03)]; // 3 + 2
        ids[10] = ENCODING_CHARS[(entropy[0] >> 1) & 0x1F]; // 5 + 0
        ids[11] = ENCODING_CHARS[((entropy[0] & 0x01) << 4) | ((entropy[1] >> 4) & 0x0F)]; // 1 + 4
        ids[12] = ENCODING_CHARS[((entropy[1] & 0x0F) << 1) | ((entropy[2] >> 7) & 0x01)]; // 4 + 1
        ids[13] = ENCODING_CHARS[(entropy[2] >> 2) & 0x1F]; // 5 + 0
        ids[14] = ENCODING_CHARS[((entropy[2] & 0x03) << 3) | (entropy[3] >> 5) & 0x07]; // 2 + 3
        ids[15] = ENCODING_CHARS[entropy[3] & 0x1F]; // 0 + 5
        ids[16] = ENCODING_CHARS[(entropy[4] >> 3) & 0x1F]; // 5 + 0
        ids[17] = ENCODING_CHARS[((entropy[4] & 0x07) << 2) | ((entropy[5] >> 6) & 0x03)]; // 3 + 2
        ids[18] = ENCODING_CHARS[(entropy[5] >> 1) & 0x1F]; // 5 + 0
        ids[19] = ENCODING_CHARS[((entropy[5] & 0x01) << 4) | ((entropy[6] >> 4) & 0x0F)]; // 1 + 4
        ids[20] = ENCODING_CHARS[((entropy[6] & 0x0F) << 1) | ((entropy[7] >> 7) & 0x01)]; // 4 + 1
        ids[21] = ENCODING_CHARS[(entropy[7] >> 2) & 0x1F]; // 5 + 0
        ids[22] = ENCODING_CHARS[((entropy[7] & 0x07) << 2) | ((entropy[8] >> 5) & 0x07)]; // 2 + 3
        ids[23] = ENCODING_CHARS[entropy[8] & 0x1F]; // 0 + 5
        ids[24] = ENCODING_CHARS[(entropy[9] >> 3) & 0x1F]; // 5 + 0
        ids[25] = ENCODING_CHARS[entropy[9] & 0x07]; // 3

        return new String(ids);
    }

    private final static class ThreadLocalHolder {
        final byte[] entropy;
        final char[] ids;
        final SecureRandom random;

        ThreadLocalHolder() {
            entropy = new byte[10];
            ids = new char[26];
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
