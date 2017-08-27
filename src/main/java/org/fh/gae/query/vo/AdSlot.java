package org.fh.gae.query.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告位
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdSlot {
    /**
     * 广告位id
     */
    private String slotId;

    /**
     * 广告位类型
     */
    private Integer slotType;

    /**
     * 广告位高
     */
    private Integer h;

    /**
     * 广告位宽
     */
    private Integer w;

    /**
     * 物料类型
     */
    private Integer[] materialType;
}
