package org.fh.gae.query.utils;

public class BitUtils {
    private BitUtils() {

    }

    /**
     * 将32位整数转换成二进制,以长度为32的字符数组表示, 高位在前, 低位在后;
     * char[0]为最高位, char[32]为最低位
     * @param number
     * @return
     */
    public static char[] toBitChars(int number) {
        char[] bits = new char[32];

        int offset = 0x01;
        for (int ix = 31; ix >= 0; --ix) {
            bits[ix] = (number & offset) > 0 ? '1' : '0';
            offset = offset << 1;
        }

        return bits;
    }
}
