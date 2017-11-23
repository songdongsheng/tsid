package com.github.songdongsheng.identifier;

import org.testng.annotations.Test;

import java.security.SecureRandom;

public class IdentifierTest {
    @Test
    public void testIdentifier() {
        Snowflake snowflake = new Snowflake(1);
        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("Snowflake: %s", snowflake.nextId()));
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("Snowflake b32: %s", snowflake.next()));
        }

        for (int i = 0; i < 10; i++) {
            String id = TSID80.next();
            System.out.println(String.format("TSID80: %s", id));
        }

        for (int i = 0; i < 10; i++) {
            String id = TSID.next();
            System.out.println(String.format("TSID: %s", id));
        }

        for (int i = 0; i < 10; i++) {
            String id = TSID120.next();
            System.out.println(String.format("TSID120: %s", id));
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("ULID: %s", ULID.next()));
        }

        for (int i = 0; i < 10; i++) {
            String uuid = RandomUUID.next();
            System.out.println(String.format("UUID: %s", uuid));
        }
    }

    @Test
    public void randTest() throws Exception {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        System.nanoTime();
        // byte[] buf = new byte[6]; // 724 w/s
        // byte[] buf = new byte[8]; // 551 w/s
        // byte[] buf = new byte[16]; // 297 w/s
        byte[] buf = new byte[6];
        for (int i = 0; i < 1000; i++)
            random.nextBytes(buf);
        long t = System.nanoTime();
        int number = 0;
        while (true) {
            random.nextBytes(buf);
            if (++number > 10_000_000) {
                break;
            }
        }
        t = System.nanoTime() - t;
        System.out.println(String.format("%s per seconds", number * 1E9 / (t)));
    }
}
