package org.fh.gae.net.vo;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fh.gae.query.vo.AdSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidRequest {
    private String requestId;

    private Auth auth;

    private Device device;

    private List<AdSlot> slots;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public List<BidRequest> splitBySlot() {
        List<BidRequest> requests = new ArrayList<>(slots.size());

        for (AdSlot slot : slots) {
            BidRequest request = new BidRequest();
            request.setRequestId(requestId);
            request.setAuth(auth);
            request.setDevice(device);
            request.setSlots(Arrays.asList(slot));

            requests.add(request);
        }

        return requests;
    }
}
