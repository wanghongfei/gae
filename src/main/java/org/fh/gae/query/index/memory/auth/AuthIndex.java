package org.fh.gae.query.index.memory.auth;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AuthIndex implements GaeIndex<AuthInfo> {
    private Map<String, AuthInfo> authInfoMap;

    public static final int LEVEL = 0;


    @PostConstruct
    public void init() {
        authInfoMap = new ConcurrentHashMap<>();

        authInfoMap.put("test", new AuthInfo("test", "test", 0, AuthStatus.NORMAL));
        authInfoMap.put("test2", new AuthInfo("test2", "test2", 0, AuthStatus.BLOCKED));
    }

    public AuthInfo fetch(String tid) {
        return authInfoMap.get(tid);
    }

    @Override
    public void add(AuthInfo authInfo) {
        authInfoMap.put(authInfo.getTid(), authInfo);
    }

    @Override
    public void update(AuthInfo authInfo) {
        add(authInfo);
    }

    @Override
    public void delete(AuthInfo authInfo) {
        authInfoMap.remove(authInfo.getTid());
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public AuthInfo packageInfo(String[] tokens) {
        AuthInfo info = new AuthInfo();
        info.setTid(tokens[2]);
        info.setToken(tokens[3]);
        info.setType(Integer.valueOf(tokens[4]));
        info.setStatus(AuthStatus.valueOf(tokens[5]));

        return info;
    }
}
