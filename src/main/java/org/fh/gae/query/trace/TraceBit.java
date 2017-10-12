package org.fh.gae.query.trace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceBit {
    private int bit;

    /**
     * 按位或
     * @param num
     * @return
     */
    public TraceBit bitOr(int num) {
        this.bit = this.bit | num;

        return this;
    }

    @Override
    public String toString() {
        return Integer.toString(this.bit, 2);
    }
}
