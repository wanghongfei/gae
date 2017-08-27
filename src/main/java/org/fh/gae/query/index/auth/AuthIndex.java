package org.fh.gae.query.index.auth;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AuthIndex implements GaeIndex<String, AuthInfo> {
    private Map<String, AuthInfo> authInfoMap;

    public static final int LEVEL = 0;


    @PostConstruct
    public void init() {
        authInfoMap = new ConcurrentHashMap<>();

        authInfoMap.put("test", new AuthInfo("test", "test", 0, AuthStatus.NORMAL));
        authInfoMap.put("test2", new AuthInfo("test2", "test2", 0, AuthStatus.BLOCKED));
    }

    @Override
    public Set<AuthInfo> trigger(int amount) {
        if (amount <= 0) {
            return Collections.emptySet();
        }

        Set<AuthInfo> infoSet = new HashSet<>();
        for (AuthInfo info : authInfoMap.values()) {
            if (amount-- <= 0) {
                break;
            }

            infoSet.add(info);
        }

        return infoSet;
    }

    @Override
    public Set<AuthInfo> trigger(Object condition) {

        AuthTriggerCondition authCon = (AuthTriggerCondition) condition;
        AuthInfo info = authInfoMap.get(authCon.getTid());

        if (null != info) {
            Set<AuthInfo> infoSet = new HashSet<>();
            infoSet.add(info);

            return infoSet;
        }

        return Collections.emptySet();
    }

    @Override
    public void add(String s, AuthInfo authInfo) {
        authInfoMap.put(s, authInfo);
    }

    @Override
    public void update(String s, AuthInfo authInfo) {
        add(s, authInfo);
    }

    @Override
    public void delete(String s, AuthInfo authInfo) {
        authInfoMap.remove(s);
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
