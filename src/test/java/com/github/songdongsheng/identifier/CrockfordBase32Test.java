package com.github.songdongsheng.identifier;

import org.testng.annotations.Test;

import java.util.Random;

public class CrockfordBase32Test {
    private static final char[] ENCODING_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
            'Y', 'Z',
    };

    private static final int[] DECODING_VALUES;

    static {
        DECODING_VALUES = new int [128];
        for (int i = 0; i < ENCODING_CHARS.length; i++) {
            char c = ENCODING_CHARS[i];
            DECODING_VALUES[c] = i;
            if (c >= 'A') DECODING_VALUES[c + ('a' - 'A')] = i;
        }
    }


    private static String encode(long id) {
        char[] ids = new char[13];
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

    @Test
    public void snowflakeTest() {
        final int testCount = 64;
        boolean passed = true;
        Snowflake snowflake = new Snowflake(new Random().nextInt(1024));
        for (int i = 0 ;i < testCount; i++) {
            long id = snowflake.nextId();
            String ids = encode(id);
            long id2 = decodeLong(ids.toCharArray());
            if (id != id2) {
                passed = false;
                System.err.println(String.format("encode/decode %s/%s error", id, id2));
            }
        }
        System.out.println(String.format("encode/decode snowflake %spassed", passed ? "" : "not "));
    }

    private long decodeLong(char[] ids) {
        if (ids == null || ids.length < 13) return -1;
        long value = DECODING_VALUES[ids[0]];
        for(int i = 1; i < 12; i++) {
            value = (value << 5) | DECODING_VALUES[ids[i]];
        }
        value = (value << 4) | DECODING_VALUES[ids[12]];
        return value;
    }

    static char[] encode(byte[] buf, char[] ids) {
        int value = 0, bit = 0, offset = 0, index = 0;
        while (true) {
            if (bit < 5) {
                if (index >= buf.length) {
                    if (bit > 0) {
                        ids[offset] = ENCODING_CHARS[value];
                    }
                    break;
                } else {
                    bit += 8;
                    value = (value << 8) | (buf[index++] & 0xFF);
                }
            }
            ids[offset++] = ENCODING_CHARS[value >> (bit - 5)];
            bit -= 5;
            if (bit == 0) {
                value = 0;
            } else {
                value &= 0xFF >> (8 - bit);
            }
        }

        return ids;
    }
/*
    private static final int[] BIT_MASK = { 0, 1, 0x03, 0x07, 0x0F };

    public static char[] encode2(byte[] buf, char[] ids) {
        int offset = 0, bitLength = buf.length << 3, bitOffset = 0;
        while(bitOffset < bitLength) {
            int q = bitOffset >>> 3;
            int r = bitOffset & 0x07;
            if (r == 0) {
                ids[offset++] = ENCODING_CHARS[(buf[q] >>> 3) & 0x1F];
            } else if (r < 3) {
                ids[offset++] = ENCODING_CHARS[(buf[q] >>> (3 - r)) & 0x1F];
            } else if (r == 3) {
                ids[offset++] = ENCODING_CHARS[buf[q] & 0x1F];
            } else {
                int x = (buf[q] & BIT_MASK[8 - r]) << (r - 3);
                int y = 0;
                if (q + 1 < buf.length) {
                    y = (buf[q + 1] >> (11 - r)) & BIT_MASK[r - 3];
                }
                ids[offset++] = ENCODING_CHARS[x | y];
            }
            bitOffset += 5;
        }
        return ids;
    }
 */
}
