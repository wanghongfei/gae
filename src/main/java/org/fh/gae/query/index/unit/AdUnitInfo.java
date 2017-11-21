package org.fh.gae.query.index.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitInfo {

    private Long userId;

    private Integer planId;

    private Integer unitId;

    private Integer status;

    private Long bid;

    private Integer priority;

    public String toIndexString() {
        final StringBuilder sb = new StringBuilder(10);
        sb.append(userId).append('\t');
        sb.append(planId).append('\t');
        sb.append(userId).append('\t');
        sb.append(status).append('\t');
        sb.append(bid).append('\t');
        sb.append(priority);
        return sb.toString();
    }
}
