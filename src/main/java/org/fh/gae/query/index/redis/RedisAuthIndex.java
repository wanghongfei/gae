package org.fh.gae.query.index.redis;

import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.index.memory.auth.AuthInfo;

public class RedisAuthIndex implements GaeIndex<AuthInfo> {
    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public AuthInfo packageInfo(String[] tokens) {
        return null;
    }

    @Override
    public void add(AuthInfo authInfo) {

    }

    @Override
    public void update(AuthInfo authInfo) {

    }

    @Override
    public void delete(AuthInfo authInfo) {

    }
}
