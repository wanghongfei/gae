package org.fh.gae.test.index;

import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.filter.FilterTable;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.index.unit.AdUnitStatus;
import org.fh.gae.query.profile.AudienceProfile;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterTableTest {

    // @Test
    public void testFilterTable() {
        List<AdUnitInfo> infos = new ArrayList<>(Arrays.asList(
                new AdUnitInfo(1L, 1, 1, AdUnitStatus.NORMAL, 1L, 1),
                new AdUnitInfo(1L, 1, 1, AdUnitStatus.PAUSE, 1L, 1)
        ));

        FilterTable.getFilter(AdUnitInfo.class).filter(infos, new RequestInfo(), new AudienceProfile());
        Assert.assertEquals(1, infos.size());
    }
}
