package org.fh.gae.net.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fh.gae.net.error.ErrCode;

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

    public static BidResponse errorOf(ErrCode error) {
        BidResponse response = new BidResponse();
        response.code = error.code();
        response.msg = error.msg();

        return response;
    }
}
