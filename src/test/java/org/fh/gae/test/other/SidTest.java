package org.fh.gae.test.other;

import org.fh.gae.query.Sid;
import org.junit.Test;

public class SidTest {
    @Test
    public void testGen() {
        String sid = Sid.genSid(120);
        System.out.println(sid);
    }

    @Test
    public void extractInstanceId() {
        String sid = "f83e31bc22814a61900bc268f784e74d135";
        String tail = sid.substring(32, sid.length());

        int num = Integer.parseInt(tail);
        System.out.println(Integer.toString(num, 2));
        int high = (num & 0x0F) << 4;
        int low = (num & 0xF0) >> 4;
        int last = high | low;
        System.out.println(Integer.toString(last, 2));

        System.out.println(high | low);
    }
}
