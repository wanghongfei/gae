package org.fh.gae.test.index;

import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.memory.unit.AdUnitIndex;
import org.fh.gae.test.BaseTestClass;
import org.junit.Assert;
import org.junit.Test;

public class DataTableTest extends BaseTestClass {
    @Test
    public void testOf() {
        int len = DataTable.of(AdUnitIndex.class).getLevel();
        Assert.assertEquals(len, AdUnitIndex.LEVEL);
    }
}
