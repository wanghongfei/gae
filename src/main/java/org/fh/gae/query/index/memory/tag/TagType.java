package org.fh.gae.query.index.memory.tag;

import org.fh.gae.query.trace.Traceable;

public enum TagType implements Traceable {
    /**
     * 性别标签
     */
    GENDER(0, 0x01),
    /**
     * 行业标签
     */
    INDUSTRY(1, 0x02);


    private int bitPos;
    private int code;

    TagType(int code, int bitPos) {
        this.code = code;
        this.bitPos = bitPos;
    }

    @Override
    public int getBitPosition() {
        return this.bitPos;
    }

    public int code() {
        return this.code;
    }
}
