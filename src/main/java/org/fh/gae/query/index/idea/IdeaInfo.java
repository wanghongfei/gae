package org.fh.gae.query.index.idea;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaInfo {
    /**
     * 创意
     */
    private String ideaId;

    /**
     * 物料类型
     */
    private Integer materialType;

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
     * 落地页地址
     */
    private String landUrl;

    /**
     * 创意状态
     */
    private Integer status;
}
