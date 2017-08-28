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
}
