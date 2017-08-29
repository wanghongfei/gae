package org.fh.gae.query.index.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagInfo {
    private Long tagId;

    private Integer unitId;

    private Integer type;
}
