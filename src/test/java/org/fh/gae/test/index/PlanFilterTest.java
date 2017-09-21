package org.fh.gae.test.index;

import org.fh.gae.query.index.filter.PlanFilter;
import org.junit.Test;

import java.math.BigInteger;

public class PlanFilterTest {

    @Test
    public void testTimeBit() {
        StringBuilder sb = new StringBuilder();
        for (int ix = 0; ix < 168; ++ix) {
            sb.append('1');
        }

        System.out.println(sb.toString());

        String bit = sb.toString();
        bit = new BigInteger(bit, 2).toString(10);

        boolean result = new PlanFilter().isTimeBitFit(bit);
        System.out.println(result);
    }
}
