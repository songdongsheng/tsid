package com.github.songdongsheng.identifier;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomUUID {
    private static final ThreadLocal<ThreadLocalCache> threadCache =
            ThreadLocal.withInitial(ThreadLocalCache::new);

    // 124 bit entropy
    public static String next() {
        ThreadLocalCache localCache = threadCache.get();
        byte[] buf = localCache.buf;
        char[] base32 = localCache.base32;
        localCache.random.nextBytes(buf);

        buf[6] &= 0x0f;  /* clear version        */
        buf[6] |= 0x40;  /* set to version 4     */
        buf[8] &= 0x3f;  /* clear variant        */
        buf[8] |= 0x80;  /* set to IETF variant  */

        return new String(CrockfordBase32.encode(buf, base32));
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

    private static class ThreadLocalCache {
        final byte[] buf;
        final char[] base32;
        final SecureRandom random;

        ThreadLocalCache() {
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