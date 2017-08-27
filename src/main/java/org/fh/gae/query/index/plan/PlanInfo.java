package org.fh.gae.query.index.plan;

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
}
