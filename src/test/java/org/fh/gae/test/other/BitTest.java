package org.fh.gae.test.other;

import org.fh.gae.query.WeightTable;
import org.fh.gae.query.index.memory.region.RegionDict;
import org.fh.gae.query.utils.BitUtils;
import org.junit.Assert;
import org.junit.Test;

public class BitTest {
    @Test
    public void testBitUtils() {
        int num = 257;
        System.out.println(BitUtils.toBitChars(num));
        System.out.println(Integer.toString(num, 2));
    }

    @Test
    public void testWeightTable() {
        int traceBit = 0x02;
        int w = WeightTable.sum(traceBit);
        Assert.assertEquals(2, w);

        traceBit = 0x01;
        w = WeightTable.sum(traceBit);
        Assert.assertEquals(1, w);

        traceBit = 0x04;
        w = WeightTable.sum(traceBit);
        Assert.assertEquals(4, w);

        traceBit = 0x04 | 0x01;
        w = WeightTable.sum(traceBit);
        Assert.assertEquals(5, w);
    }

    @Test
    public void testIp2long() {
        String ip = "1.1.1.8";
        long ipL = RegionDict.ip2long(ip);
        System.out.println(Long.toString(ipL, 2));
    }
}
