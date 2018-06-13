package org.fh.gae.query.index.filter;

import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.idea.IdeaAuditIndex;
import org.fh.gae.query.index.idea.IdeaAuditInfo;
import org.fh.gae.query.index.idea.IdeaAuditStatus;
import org.fh.gae.query.index.idea.IdeaInfo;
import org.fh.gae.query.index.idea.IdeaStatus;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.vo.AdSlot;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@DependsOn("filterTable")
public class IdeaFilter implements GaeFilter<IdeaInfo> {
    @PostConstruct
    public void init() {
        FilterTable.register(IdeaInfo.class, this);
    }

    @Override
    public void filter(Collection<IdeaInfo> elems, RequestInfo request, AudienceProfile profile) {
        traverse(
                elems,
                ideaInfo -> isStatusFit(ideaInfo)
                        && isMaterialTypeFit(ideaInfo, request)
                        && isSizeFit(ideaInfo, request)
                        && isAuditFit(ideaInfo, request)
        );
    }

    private boolean isStatusFit(IdeaInfo info) {
        return info.getStatus() == IdeaStatus.PASS;
    }

    private boolean isMaterialTypeFit(IdeaInfo ideaInfo, RequestInfo request) {
        Set<Integer> requriedTypeSet = new HashSet<>();
        for (Integer type : request.getSlot().getMaterialType()) {
            requriedTypeSet.add(type);
        }

        return requriedTypeSet.contains(ideaInfo.getMaterialType());

    }

    protected boolean isSizeFit(IdeaInfo info, RequestInfo request) {
        AdSlot slot = request.getSlot();
        Integer requiredHeight = slot.getH();
        Integer requiredWidth = slot.getW();

        return requiredHeight.equals(info.getH()) && requiredWidth.equals(info.getW());
    }

    protected boolean isAuditFit(IdeaInfo ideaInfo, RequestInfo request) {
        IdeaAuditInfo auditInfo = DataTable.of(IdeaAuditIndex.class).getAuditInfo(ideaInfo.getIdeaId());
        if (null == auditInfo
                || auditInfo.getStatus() != IdeaAuditStatus.PASS
                || !auditInfo.getTid().equals(request.getAuth().getTid())) {
            return false;
        }

        return true;
    }
}
