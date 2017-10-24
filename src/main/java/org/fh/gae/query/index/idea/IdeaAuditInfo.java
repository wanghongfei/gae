package org.fh.gae.query.index.idea;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaAuditInfo {
    /**
     * 创意id
     */
    private String ideaId;

    /**
     * 媒体审核完成后分配的创意id
     */
    private String adCode;

    private Integer status;
}
