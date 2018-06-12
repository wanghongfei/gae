package org.fh.gae.query.index.memory.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanInfo {
    private Integer planId;
    private Long userId;
    private Integer stauts;
    private String timeBit;

    public String toIndexString() {
        final StringBuilder sb = new StringBuilder(10);
        sb.append(planId).append('\t');
        sb.append(userId).append('\t');
        sb.append(stauts).append('\t');
        sb.append(timeBit);
        return sb.toString();
    }
}
