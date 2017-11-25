package org.fh.gae.test;

import org.fh.gae.net.vo.Auth;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.Device;
import org.fh.gae.query.vo.AdSlot;

import java.util.Arrays;

public class RequestBuilder {

    public static BidRequest buildRequest() {
        BidRequest req = new BidRequest();
        req.setRequestId("test-case");

        Auth auth = new Auth("tid", "token");
        req.setAuth(auth);

        Device device = new Device();
        device.setMac("mac");
        device.setIp("127.0.0.1");
        req.setDevice(device);

        AdSlot slot = new AdSlot();
        slot.setW(1920);
        slot.setH(1080);
        slot.setMaterialType(new Integer[] {1});
        slot.setSlotId("slot-id");
        slot.setSlotType(1);

        req.setSlots(Arrays.asList(slot));

        return req;
    }
}
