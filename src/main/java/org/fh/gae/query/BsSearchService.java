package org.fh.gae.query;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.query.vo.Ad;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class BsSearchService {
    public List<Ad> fetchAds(BidRequest request) {
        return Arrays.asList(
                new Ad("ad1", "http://www.baidu.com"),
                new Ad("ad2", "http://www.sina.com.cn")
        );
    }
}
