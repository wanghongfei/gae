package org.fh.gae.log;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchLog implements SerializableLog {
    // 广告展现id
    private String sid = "-";
    // 检索时间戳
    private long timestamp = 0;
    // 流量方id
    private String tid = "-";
    // 流量方请求id
    private String requestId = "-";
    // 资源类型
    private long resourceType = 0;
    // 广告位编码
    private String slotCode = "-";
    //  广告位类型
    private long slotType = 0;
    //  广告位宽
    private int width = 0;
    //  广告位高
    private int height = 0;
    //  物料类型
    private String materialType = "-";
    //  设备mac
    private String mac = "-";
    //  设备ip
    private String ip = "-";
    //  推广计划id
    private int planId = 0;
    //  推广单元id
    private int unitId = 0;
    //  创意id
    private String ideaId = "-";
    // 广告唯一标识
    private String adCode = "-";
    // 命中的标签信息,tagType:tagId逗号分隔, 如 1:1200,2:2100,3:4500
    private String tagIds = "-";
    // 地域id
    private int regionId = 0;


    @Override
    public byte[] serializeLog() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(50);
        sb.append(sid).append('\t');
        sb.append(timestamp).append('\t');
        sb.append(tid).append('\t');
        sb.append(requestId).append('\t');
        sb.append(resourceType).append('\t');
        sb.append(slotCode).append('\t');
        sb.append(slotType).append('\t');
        sb.append(width).append('\t');
        sb.append(height).append('\t');
        sb.append(materialType).append('\t');
        sb.append(mac).append('\t');
        sb.append(ip).append('\t');
        sb.append(planId).append('\t');
        sb.append(unitId).append('\t');
        sb.append(ideaId).append('\t');
        sb.append(adCode).append('\t');
        sb.append(tagIds).append('\t');
        sb.append(regionId);

        return sb.toString();
    }
}
