package com.github.songdongsheng.identifier;

class CrockfordBase32Test {
    private static final char[] ENCODING_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
            'Y', 'Z',
    };

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
