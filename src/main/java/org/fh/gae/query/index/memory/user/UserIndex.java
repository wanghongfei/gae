package org.fh.gae.query.index.memory.user;

import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广告主账户信息索引
 */
@Component
public class UserIndex implements GaeIndex<UserInfo> {
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
    public void add(UserInfo userInfo) {
        userInfoMap.put(userInfo.getUserId(), userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        add(userInfo);
    }

    @Override
    public void delete(UserInfo userInfo) {
        userInfoMap.remove(userInfo.getUserId());
    }
}
