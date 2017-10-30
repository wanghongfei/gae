package org.fh.gae.query.index.region;

import org.springframework.stereotype.Component;

/**
 * 地域字典
 */
@Component
public class RegionDict {
    public IPRegion[] ipRegions;

    public IPRegion match(LongValue ip) {
        return doMatch(0, ipRegions.length - 1, ip);
    }

    /**
     * 二分查找ip属于哪个region
     * @param start
     * @param end
     * @param ip
     * @return
     */
    private IPRegion doMatch(int start, int end, LongValue ip) {
        if (start >= end) {
            return null;
        }

        if (0 == ipRegions[start].compareTo(ip)) {
            return ipRegions[start];
        }
        if (0 == ipRegions[end].compareTo(ip)) {
            return ipRegions[end];
        }

        int middle = (start + end) / 2;
        int diff = ipRegions[middle].compareTo(ip);

        if (diff == 0) {
            return ipRegions[middle];
        }

        if (diff > 0) {
            return doMatch(start, middle - 1, ip);
        }

        return doMatch(middle + 1, end, ip);
    }
}
