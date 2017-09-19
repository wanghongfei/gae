package org.fh.gae.query.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 受众画像
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudienceProfile {
    private Map<Integer, Set<Long>> tagMap;
}
