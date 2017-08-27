package org.fh.gae.query.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回的广告信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ad {
    /**
     * 广告id
     */
    private String adId;

    /**
     * 广告位id
     */
    private String slotId;

    /**
     * 物料地址
     */
    private String url;

    /**
     * 物料宽(px)
     */
    private Integer w;

    /**
     * 物料高
     */
    private Integer h;

    /**
     * 曝光监测地址
     */
    private String[] showUrls;

    /**
     * 点击监测地址
     */
    private String[] ckUrls;

    /**
     * 落地页地址
     */
    private String landUrl;
}
