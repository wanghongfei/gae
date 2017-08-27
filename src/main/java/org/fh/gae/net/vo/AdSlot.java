package org.fh.gae.net.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdSlot {
    private String slotCode;

    private Integer slotType;

    private Integer width;

    private Integer height;

    private Integer[] ideaTypes;
}
