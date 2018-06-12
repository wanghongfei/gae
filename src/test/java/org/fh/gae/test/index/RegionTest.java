package org.fh.gae.test.index;

import org.fh.gae.query.index.memory.region.IPRegion;
import org.fh.gae.query.index.memory.region.LongValue;
import org.fh.gae.query.index.memory.region.RegionDict;
import org.junit.Assert;
import org.junit.Test;

public class RegionTest {
    @Test
    public void testMatch() {
        // init data
        IPRegion[] regions = new IPRegion[] {
                new IPRegion(0, 100, 0, 0),
                new IPRegion(101, 200, 0, 1),
                new IPRegion(201, 300, 0, 2),
                new IPRegion(301, 400, 0, 3),
                new IPRegion(401, 500, 0, 4),
        };

        RegionDict dict = new RegionDict();
        dict.ipRegions = regions;


        LongValue ip = new LongValue(250);
        IPRegion target = dict.match(ip);
        Assert.assertEquals(new IPRegion(201, 300, 0, 2), target);

        ip = new LongValue(401);
        target = dict.match(ip);
        Assert.assertEquals(new IPRegion(401, 500, 0, 4), target);

        ip = new LongValue(400);
        target = dict.match(ip);
        Assert.assertEquals(new IPRegion(301, 400, 0, 3), target);

        ip = new LongValue(500);
        target = dict.match(ip);
        Assert.assertEquals(new IPRegion(401, 500, 0, 4), target);

        ip = new LongValue(501);
        target = dict.match(ip);
        Assert.assertNull(target);

        ip = new LongValue(-10);
        target = dict.match(ip);
        Assert.assertNull(target);

        ip = new LongValue(0);
        target = dict.match(ip);
        Assert.assertEquals(new IPRegion(0, 100, 0, 0), target);

        ip = new LongValue(100);
        target = dict.match(ip);
        Assert.assertEquals(new IPRegion(0, 100, 0, 0), target);

        ip = new LongValue(101);
        target = dict.match(ip);
        Assert.assertEquals(new IPRegion(101, 200, 0, 1), target);

    }
}
