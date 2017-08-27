package org.fh.gae.net.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fh.gae.query.vo.Ad;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResult {
    /**
     * 请求id
     */
    private String requestId;

    /**
     * 广告
     */
    private List<Ad> ads;
}
