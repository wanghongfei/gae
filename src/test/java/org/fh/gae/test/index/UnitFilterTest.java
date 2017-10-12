package org.fh.gae.test.index;

import org.fh.gae.query.index.filter.UnitFilter;
import org.fh.gae.query.index.tag.TagType;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.index.unit.AdUnitStatus;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.test.BaseTestClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnitFilterTest extends BaseTestClass {
    @Autowired
    private UnitFilter unitFilter;

    @Test
    public void testGenderFilter() {
        // mock
        Map<Integer, Set<Long>> tags = new HashMap<>();
        Set<Long> tagSet = new HashSet<>();
        tagSet.add(1L);
        tags.put(TagType.GENDER.code(), tagSet);

        AudienceProfile profile = new AudienceProfile(tags);


        Set<AdUnitInfo> infoSet = new HashSet<>();
        AdUnitInfo info1 = new AdUnitInfo();
        info1.setStatus(AdUnitStatus.NORMAL);
        info1.setUnitId(100);
        infoSet.add(info1);

        AdUnitInfo info2 = new AdUnitInfo();
        info2.setStatus(AdUnitStatus.NORMAL);
        info2.setUnitId(200);
        infoSet.add(info2);

        unitFilter.filter(infoSet, null, profile);
        System.out.println(infoSet);
    }
}
