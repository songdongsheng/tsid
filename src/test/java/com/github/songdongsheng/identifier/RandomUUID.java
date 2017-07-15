package com.github.songdongsheng.identifier;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomUUID {
    private static final ThreadLocal<ThreadLocalHolder> threadLocalHolder =
            ThreadLocal.withInitial(ThreadLocalHolder::new);

    // 124 bit entropy
    public static String next() {
        ThreadLocalHolder threadLocal = threadLocalHolder.get();
        byte[] buf = threadLocal.buf;
        char[] base32 = threadLocal.base32;
        threadLocal.random.nextBytes(buf);

        buf[6] &= 0x0f;  /* clear version        */
        buf[6] |= 0x40;  /* set to version 4     */
        buf[8] &= 0x3f;  /* clear variant        */
        buf[8] |= 0x80;  /* set to IETF variant  */

        return new String(CrockfordBase32Test.encode(buf, base32));
/*
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (buf[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (buf[i] & 0xff);
        return new UUID(msb, lsb);
 */
    }

    private static class ThreadLocalHolder {
        final byte[] buf;
        final char[] base32;
        final SecureRandom random;

        ThreadLocalHolder() {
            buf = new byte[16];
            base32 = new char[26];
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
