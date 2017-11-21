package org.fh.gae.net.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {
    private int code = 0;

    private String msg;

    private long ms;

    private BidResult result;

    public BidResponse(BidResult result, long ms) {
        this.result = result;
        this.ms = ms;
    }

    public BidResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private static final BidResponse errorResp = new BidResponse(-1, "error", 0L, null);

    public static BidResponse error() {
        return errorResp;
    }
}
