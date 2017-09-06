package org.fh.gae.query.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 受众画像
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudienceProfile {
    private List<Long> tagIds;
}
