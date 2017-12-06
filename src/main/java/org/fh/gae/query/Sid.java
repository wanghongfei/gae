package org.fh.gae.query;

import java.util.UUID;

public class Sid {
    public static String genSid(int instanceId) {
        int base = instanceId % 256;
        int high = (base & 0x0F) << 4;
        int low = (base & 0xF0) >> 4;
        int x = high | low;

        return UUID.randomUUID().toString().replaceAll("-", "") + x;
    }
}
