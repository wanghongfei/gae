package org.fh.gae.net.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fh.gae.query.vo.AdSlot;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo {

    private String requestId;

    private Auth auth;

    private Device device;

    private AdSlot slot;

    public RequestInfo(BidRequest req, int slotIndex) {
        this.requestId = req.getRequestId();
        this.auth = req.getAuth();
        this.device = req.getDevice();
        this.slot = req.getSlots().get(slotIndex);
    }
}
