package org.fh.gae.query.index.idea;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitIdeaRelInfo {
    private Integer unitId;
    private String ideaId;

    public String toIndexString() {
        final StringBuilder sb = new StringBuilder(10);
        sb.append(unitId).append('\t');
        sb.append(ideaId);
        return sb.toString();
    }
}
