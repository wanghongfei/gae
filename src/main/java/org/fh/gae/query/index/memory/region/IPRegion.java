package org.fh.gae.query.index.memory.region;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IPRegion implements Comparable<LongValue> {
    private long startIp;

    private long endIp;

    private int l1Region;

    private int l2Region;

    @Override
    public int compareTo(LongValue ip) {
        long targetIp = ip.getValue();

        if (targetIp >= startIp && targetIp <= endIp) {
            return 0;
        }

        if (targetIp < startIp) {
            return 1;
        }

        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPRegion ipRegion = (IPRegion) o;

        if (startIp != ipRegion.startIp) return false;
        if (endIp != ipRegion.endIp) return false;
        if (l1Region != ipRegion.l1Region) return false;
        return l2Region == ipRegion.l2Region;
    }
}
