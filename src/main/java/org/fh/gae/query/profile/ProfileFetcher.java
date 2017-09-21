package org.fh.gae.query.profile;

import org.fh.gae.net.vo.BidRequest;

public interface ProfileFetcher {
    AudienceProfile fetchProfile(BidRequest request);
}
