package org.fh.gae.query.index.user;

import org.fh.gae.query.index.GaeIndex;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广告主账户信息索引
 */
public class UserIndex implements GaeIndex<Long, UserInfo> {
    public static final int LEVEL = 1;

    /**
     * 广告主账户id -> 账户信息
     */
    private Map<Long, UserInfo> userInfoMap;

    @PostConstruct
    public void init() {
        userInfoMap = new ConcurrentHashMap<>();
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public UserInfo packageInfo(String[] tokens) {
        Long userId = Long.valueOf(tokens[2]);
        Integer status = Integer.valueOf(tokens[3]);

        return new UserInfo(userId, status);
    }

    @Override
    public void add(Long key, UserInfo userInfo) {
        userInfoMap.put(key, userInfo);
    }

    @Override
    public void update(Long key, UserInfo userInfo) {
        add(key, userInfo);
    }

    @Override
    public void delete(Long key, UserInfo userInfo) {
        userInfoMap.remove(key);
    }
}
