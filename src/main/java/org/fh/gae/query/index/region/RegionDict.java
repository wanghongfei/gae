package org.fh.gae.query.index.region;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeDictProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 地域字典
 */
@Component
@Slf4j
public class RegionDict {
    public IPRegion[] ipRegions;

    @Autowired
    private GaeDictProps dictProps;

    public IPRegion match(String ip) {
        long ipL = ip2long(ip);
        return match(new LongValue(ipL));
    }

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

    /**
     * 加载IP字典
     * @throws IOException
     */
    @PostConstruct
    private void loadIpDict() throws IOException {
        log.info("start loading ip dict");

        try (FileInputStream fis = new FileInputStream(dictProps.getIp())) {
            InputStreamReader r = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(r);

            List<IPRegion> ipList = new ArrayList<>(300);

            String line;
            while ( (line = reader.readLine()) != null ) {
                loadLine(line, ipList);
            }

            this.ipRegions = ipList.toArray(new IPRegion[0]);
            log.info("{} items loaded", ipList.size());

        } catch (IOException e) {
            throw e;
        }
    }

    private void loadLine(String line, List<IPRegion> dictList) {
        String[] terms = line.split("\t");
        if (terms.length < 4) {
            log.warn("invalid ipdict:{}", line);
        }

        String startIpStr = terms[0];
        long startIp = ip2long(startIpStr);
        String endIpStr = terms[1];
        long endIp = ip2long(endIpStr);

        if (startIp == -1 || endIp == -1) {
            log.warn("invalid ipdict:{}", line);
            return;
        }

        int l1Region = Integer.parseInt(terms[2]);
        int l2Region = Integer.parseInt(terms[3]);

        dictList.add(new IPRegion(startIp, endIp, l1Region, l2Region));

    }

    public static long ip2long(String ip) {
        String[] parts = ip.split("\\.");
        if (null == parts || 0 == parts.length || parts.length < 4) {
            return -1;
        }

        int a = Integer.parseInt(parts[0]);
        int b = Integer.parseInt(parts[1]);
        int c = Integer.parseInt(parts[2]);
        int d = Integer.parseInt(parts[3]);

        long result = 0;
        result = result | d;
        result = result | (c << 8);
        result = result | (b << 16);
        result = result | (a << 24);

        return result;
    }
}
