package com.github.songdongsheng.identifier;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SortedBase64Test {
    private Snowflake snowflake = new Snowflake(1);

    @BeforeClass
    public void setUp() {
    }

    @AfterClass
    public void tearDown() {
    }

    @Test
    public void testSnowflake() {
        for (int i = 0; i < 12; i++) {
            byte[] buf = next();
            String b64 = SortedBase64.getEncoder().encode(buf);
            byte[] decoded = SortedBase64.getDecoder().decode(b64);
            if (buf.length != decoded.length) {
                throw new RuntimeException("SortedBase64 encode/decode failed");
            }

            for(int n = 0; n < buf.length; n++) {
                if (buf[n] != decoded[n]) {
                    throw new RuntimeException("SortedBase64 encode/decode failed");
                }
            }

            System.out.println(b64 + " -> " + Base64.getUrlEncoder().withoutPadding().encodeToString(buf));
        }
    }

    @Test
    public void testRandomId() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        for(int m = 4; m <= 8; m++) {
            byte[] buf = new byte[m];
            System.out.println("\nTest for " + m + " bytes");
            for (int i = 0; i < 12; i++) {
                secureRandom.nextBytes(buf);
                String b64 = SortedBase64.getEncoder().encode(buf);
                byte[] decoded = SortedBase64.getDecoder().decode(b64);
                if (buf.length != decoded.length) {
                    throw new RuntimeException("SortedBase64 encode/decode failed");
                }

                for (int n = 0; n < buf.length; n++) {
                    if (buf[n] != decoded[n]) {
                        throw new RuntimeException("SortedBase64 encode/decode failed");
                    }
                }

                System.out.println(b64 + " -> " + Base64.getUrlEncoder().withoutPadding().encodeToString(buf));
            }
        }
    }

    private byte[] next() {
        long x = snowflake.nextId();
        byte[] buf = new byte[8];
        for(int i = 0; i < buf.length; i++) {
            buf[i] = (byte)(x >>> ((7 - i) << 3));
        }
        return buf;
    }
}
