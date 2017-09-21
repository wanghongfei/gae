package org.fh.gae.query.index.idea;

import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdeaIndex implements GaeIndex<IdeaInfo> {
    public static final int LEVEL = 4;

    // [创意id] -> [创意信息]
    private Map<String, IdeaInfo> infoMap;


    @PostConstruct
    public void init() {
        infoMap = new ConcurrentHashMap<>();
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 7;
    }

    public Set<IdeaInfo> fetchInfo(Set<String> idSet) {
        Set<IdeaInfo> resultSet = new HashSet<>(idSet.size() + idSet.size() / 3);

        idSet.forEach(id -> resultSet.add(infoMap.get(id)));

        return resultSet;
    }

    @Override
    public IdeaInfo packageInfo(String[] tokens) {
        String ideaId = tokens[2];
        Integer type = Integer.valueOf(tokens[3]);
        String url = tokens[4];
        Integer w = Integer.valueOf(tokens[5]);
        Integer h = Integer.valueOf(tokens[6]);
        String landUrl = tokens[7];
        Integer st = Integer.valueOf(tokens[8]);

        IdeaInfo info = new IdeaInfo();
        info.setIdeaId(ideaId);
        info.setMaterialType(type);
        info.setUrl(url);
        info.setW(w);
        info.setH(h);
        info.setLandUrl(landUrl);
        info.setStatus(st);

        return info;
    }

    @Override
    public void add(IdeaInfo ideaInfo) {
        infoMap.put(ideaInfo.getIdeaId(), ideaInfo);
    }

    @Override
    public void update(IdeaInfo ideaInfo) {
        add(ideaInfo);
    }

    @Override
    public void delete(IdeaInfo ideaInfo) {
        infoMap.remove(ideaInfo.getIdeaId());
    }
}
