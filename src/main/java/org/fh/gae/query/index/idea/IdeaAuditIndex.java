package org.fh.gae.query.index.idea;

import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdeaAuditIndex implements GaeIndex<IdeaAuditInfo> {
    public static final int LEVEL = 8;

    /**
     * ideaId -> 创意审核信息
     */
    private Map<String, IdeaAuditInfo> ideaAuditMap;

    @PostConstruct
    public void init() {
        ideaAuditMap = new ConcurrentHashMap<>();
    }

    public IdeaAuditInfo getAuditInfo(String ideaId) {
        return ideaAuditMap.get(ideaId);
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public IdeaAuditInfo packageInfo(String[] tokens) {
        String ideaId = tokens[2];
        String adCode = tokens[3];
        Integer status = Integer.valueOf(tokens[4]);

        return new IdeaAuditInfo(ideaId, adCode, status);
    }

    @Override
    public void add(IdeaAuditInfo ideaAuditInfo) {
        ideaAuditMap.put(ideaAuditInfo.getIdeaId(), ideaAuditInfo);
    }

    @Override
    public void update(IdeaAuditInfo ideaAuditInfo) {
        add(ideaAuditInfo);
    }

    @Override
    public void delete(IdeaAuditInfo ideaAuditInfo) {
        ideaAuditMap.remove(ideaAuditInfo.getIdeaId());
    }
}
