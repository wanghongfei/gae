package org.fh.gae.query.index.memory.region;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionInfo implements Comparable<RegionInfo> {
    private Integer unitId;

    private Integer l1Region = -1;

    private Integer l2Region = -1;

    @Override
    public int compareTo(RegionInfo o) {
        return l1Region + l2Region - o.l1Region - o.l2Region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RegionInfo that = (RegionInfo) o;

        if (unitId != null ? !unitId.equals(that.unitId) : that.unitId != null) return false;
        if (l1Region != null ? !l1Region.equals(that.l1Region) : that.l1Region != null) return false;
        return l2Region != null ? l2Region.equals(that.l2Region) : that.l2Region == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (unitId != null ? unitId.hashCode() : 0);
        result = 31 * result + (l1Region != null ? l1Region.hashCode() : 0);
        result = 31 * result + (l2Region != null ? l2Region.hashCode() : 0);
        return result;
    }
}
