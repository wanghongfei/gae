package org.fh.gae.query.index.idea;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.vo.Ad;

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

    /**
     * 曝光监测地址
     */
    private String[] showUrls;

    public Ad toAd(String slotId) {
        Ad ad = new Ad();

        ad.setAdId(ideaId);
        ad.setMaterialType(materialType);
        ad.setUrl(url);
        ad.setW(w);
        ad.setH(h);
        ad.setLandUrl(landUrl);
        ad.setSlotId(slotId);
        ad.setShowUrls(showUrls);

        String adCode = DataTable.of(IdeaAuditIndex.class).getAuditInfo(ideaId).getAdCode();
        ad.setAdCode(adCode);

        return ad;
    }
}
