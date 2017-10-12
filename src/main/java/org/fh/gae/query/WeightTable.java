package org.fh.gae.query;

import org.fh.gae.query.utils.BitUtils;

public class WeightTable {
    /**
     * 权重表, int[index]表示二进制int中第index+1位的权重
     */
    private static int[] weightMap = new int[] {
            1, 2, 4, 8,
            16, 32, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
    };

    /**
     * 根据追踪位计算总权重
     * @param traceNum
     * @return
     */
    public static int sum(int traceNum) {
        char[] bits = BitUtils.toBitChars(traceNum);

        int sum = 0;
        for (int ix = 31; ix >= 0; --ix) {
            if (bits[ix] == '1') {
                int weight = weightMap[31 - ix];
                sum += weight;
            }
        }

        return sum;
    }
}
