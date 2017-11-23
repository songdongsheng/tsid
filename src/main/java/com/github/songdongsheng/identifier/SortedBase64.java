package com.github.songdongsheng.identifier;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Sorted Base64 without padding bytes.
 * <p>
 * URL safe characters include uppercase and lowercase letters, decimal digits,
 * hyphen, period, underscore, and tilde, i.e. ALPHA / DIGIT / "-" / "." / "_" / "~".
 * <p>
 * Sorted Base64 encoding scheme use '-', decimal digits, uppercase letters, '_',
 * lowercase letters, in the ASCII table order.
 */
public class SortedBase64 {

    private SortedBase64() {
    }

    /**
     * Returns a {@link Encoder} that encodes using the
     * Sorted base64 encoding scheme.
     *
     * @return A Sorted Base64 encoder.
     */
    public static Encoder getEncoder() {
        return new Encoder();
    }

    /**
     * Returns a {@link Decoder} that decodes using the
     * Sorted base64 encoding scheme.
     *
     * @return A Sorted Base64 decoder.
     */
    public static Decoder getDecoder() {
        return new Decoder();
    }

    /**
     * This class implements an encoder for encoding byte data using
     * the Sorted Base64 {@link SortedBase64} encoding scheme.
     * <p>
     * <p> Instances of {@link Encoder} class are safe for use by
     * multiple concurrent threads.
     */
    public static class Encoder {

        /**
         * This array is a lookup table that translates 6-bit positive integer
         * index values into their "Sorted Base64 Alphabet".
         */
        private static final char[] toSortedBase64 = {
                '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        };

        private Encoder() {
        }

        /**
         * Encodes the specified byte array into a String using the {@link SortedBase64}
         * encoding scheme.
         */
        public String encode(byte[] src) {
            byte[] encoded = new byte[outLength(src.length)];
            encode(src, 0, encoded, 0, src.length);
            return new String(encoded, 0, encoded.length, StandardCharsets.ISO_8859_1);
        }

        private static int outLength(int srcLen) {
            return (srcLen * 8 + 5) / 6;
        }

        /**
         * Encodes an array from the specified source array, beginning at the
         * specified position, to the specified position of the destination array,
         * using the {@link SortedBase64} encoding scheme. The number of bytes
         * encoded is equal to the <code>length</code> argument. The returned
         * value is of the length of the resulting bytes.
         */
        public int encode(byte[] src, int srcPos, byte[] dst, int dstPos, int length) {
            if (srcPos + length > src.length || outLength(length) + dstPos > dst.length) {
                throw new IllegalArgumentException("Output byte array is too small for encoding all input bytes");
            }

            int sp = srcPos;
            int s1 = srcPos + (length / 3) * 3;
            int dp = dstPos;
            while (sp < s1) {
                int bits = (src[sp++] & 0xff) << 16 | (src[sp++] & 0xff) << 8 | (src[sp++] & 0xff);

                dst[dp++] = (byte) toSortedBase64[(bits >>> 18) & 0x3f];
                dst[dp++] = (byte) toSortedBase64[(bits >>> 12) & 0x3f];
                dst[dp++] = (byte) toSortedBase64[(bits >>> 6) & 0x3f];
                dst[dp++] = (byte) toSortedBase64[bits & 0x3f];
            }

            int s2 = srcPos + length;
            if (s1 < s2) {
                // 1 or 2 leftover bytes
                int b0 = src[sp++] & 0xff;
                dst[dp++] = (byte) toSortedBase64[b0 >> 2];
                if (sp == s2) {
                    dst[dp++] = (byte) toSortedBase64[(b0 << 4) & 0x3f];
                } else {
                    int b1 = src[sp] & 0xff;
                    dst[dp++] = (byte) toSortedBase64[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[dp++] = (byte) toSortedBase64[(b1 << 2) & 0x3f];
                }
            }

            return dp - dstPos;
        }
    }

    /**
     * This class implements a decoder for decoding byte data using the
     * Sorted Base64 {@link SortedBase64} encoding scheme.
     * <p>
     * <p> Instances of {@link Decoder} class are safe for use by
     * multiple concurrent threads.
     */
    public static class Decoder {

        /**
         * Lookup table for decoding unicode characters drawn from the
         * "Sorted Base64 Alphabet" into their 6-bit positive integer
         * equivalents. Characters that are not in the Sorted Base64
         * alphabet but fall within the bounds of the array are encoded to -1.
         */
        private static final int[] fromSortedBase64 = new int[128];

        static {
            Arrays.fill(fromSortedBase64, -1);
            for (int i = 0; i < SortedBase64.Encoder.toSortedBase64.length; i++)
                fromSortedBase64[SortedBase64.Encoder.toSortedBase64[i]] = i;
        }

        private Decoder() {
        }

        /**
         * Decodes a Base64 encoded String into a newly-allocated byte array
         * using the {@link SortedBase64} encoding scheme.
         */
        public byte[] decode(String b64) {
            byte[] src = b64.getBytes(StandardCharsets.ISO_8859_1);
            byte[] dst = new byte[outLength(src.length)];
            int ret = decode(src, 0, dst, 0, src.length);
            if (ret != dst.length) {
                dst = Arrays.copyOf(dst, ret);
            }
            return dst;
        }

        private static int outLength(int srcLen) {
            return (srcLen * 6) / 8;
        }

        /**
         * Decodes an array from the specified source array, beginning at the
         * specified position, to the specified position of the destination array,
         * using the {@link SortedBase64} encoding scheme. The number of bytes
         * decoded is equal to the <code>length</code> argument. The returned
         * value is of the length of the resulting bytes.
         */
        public int decode(byte[] src, int srcPos, byte[] dst, int dstPos, int length) {
            if (length < 2 || length % 4 == 1) {
                throw new IllegalArgumentException("Input byte array has invalid length for decoding");
            }

            if (srcPos + length > src.length || outLength(length) + dstPos > dst.length) {
                throw new IllegalArgumentException("Output byte array is too small for decoding all input bytes");
            }

            int sp = srcPos;
            int s1 = srcPos + (length / 4) * 4;
            int dp = dstPos;
            while (sp < s1) {
                int bits = (fromSortedBase64[src[sp++]] & 0x3f) << 18 | (fromSortedBase64[src[sp++]] & 0x3f) << 12
                        | (fromSortedBase64[src[sp++]] & 0x3f) << 6 | (fromSortedBase64[src[sp++]] & 0x3f);

                dst[dp++] = (byte) (bits >> 16);
                dst[dp++] = (byte) (bits >> 8);
                dst[dp++] = (byte) (bits);
            }

            int s2 = srcPos + length;
            if (s1 < s2) {
                // 2 or 3 leftover bytes
                int b0 = (fromSortedBase64[src[sp++]] & 0x3f) << 6 | (fromSortedBase64[src[sp++]] & 0x3f);
                if (sp == s2) {
                    dst[dp++] = (byte) (b0 >> 4);
                } else {
                    int b1 = (b0 << 4) | ((fromSortedBase64[src[sp]] & 0x3f) >> 2);
                    dst[dp++] = (byte) (b1 >> 8);
                    dst[dp++] = (byte) (b1);
                }
            }

            return dp - dstPos;
        }
    }
}
