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

    private Object result;

    public BidResponse(Object result) {
        this.result = result;
    }

    private static final BidResponse errorResp = new BidResponse(-1, "error", null);

    public static BidResponse error() {
        return errorResp;
    }
}
