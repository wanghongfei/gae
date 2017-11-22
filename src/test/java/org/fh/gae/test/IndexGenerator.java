package org.fh.gae.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.fh.gae.query.index.idea.IdeaAuditInfo;
import org.fh.gae.query.index.idea.IdeaAuditStatus;
import org.fh.gae.query.index.idea.IdeaInfo;
import org.fh.gae.query.index.idea.IdeaStatus;
import org.fh.gae.query.index.idea.UnitIdeaRelInfo;
import org.fh.gae.query.index.plan.PlanInfo;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.index.unit.AdUnitStatus;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全量索引生成器
 */
public class IndexGenerator {
    private static final Long USER_ID = 1L;

    /**
     * 生成全量索引文件
     * @throws IOException
     */
    @Test
    public void genIndex() throws IOException {
        FileOutputStream fos = new FileOutputStream("mock-idx.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        String mediaIndex = "0\t0\ttid\ttoken\t0\tNORMAL\n";
        String userIndex = "1\t0\t100\t0\n";

        bos.write(mediaIndex.getBytes());
        bos.write(userIndex.getBytes());

        // 生成计划
        for (PlanInfo info : genPlan(1000)) {
            bos.write(("2\t0\t" + info.toIndexString() + "\n").getBytes());

            // 单元
            for (AdUnitInfo unit : genUnit(info.getPlanId(), 2)) {
                bos.write(("3\t0\t" + unit.toIndexString() + "\n").getBytes());

                // 创意
                for (IdeaInfo idea : genIdea(unit.getUnitId(), 2)) {
                    bos.write(("4\t0\t" + idea.toIndexString() + "\n").getBytes());

                    // 关联
                    UnitIdeaRelInfo rel = new UnitIdeaRelInfo(unit.getUnitId(), idea.getIdeaId());
                    bos.write(("5\t0\t" + rel.toIndexString() + "\n").getBytes());

                    // 审核
                    IdeaAuditInfo audit = new IdeaAuditInfo(
                            idea.getIdeaId(),
                            "adcode-" + RandomStringUtils.randomAlphabetic(10),
                            IdeaAuditStatus.PASS,
                            "tid"
                    );
                    bos.write(("8\t0\t" + audit.toIndexString() + "\n").getBytes());
                }
            }

            bos.flush();
        }

        // 生成单元

        bos.flush();
        bos.close();
    }

    private List<PlanInfo> genPlan(int tot) {
        List<PlanInfo> infos = new ArrayList<>(tot);
        for (int ix = 0; ix < tot; ++ix) {
            PlanInfo info = new PlanInfo();
            info.setPlanId(RandomUtils.nextInt(1, tot * 100));
            info.setStauts(0);
            info.setTimeBit("374144419156711147060143317175368453031918731001855");
            info.setUserId(USER_ID);

            infos.add(info);
        }

        return infos;
    }

    private List<AdUnitInfo> genUnit(Integer planId, int tot) {
        List<AdUnitInfo> infos = new ArrayList<>(tot);
        for (int ix = 0; ix < tot; ++ix) {
            AdUnitInfo info = new AdUnitInfo();
            info.setUnitId(RandomUtils.nextInt(1, 10000));
            info.setStatus(AdUnitStatus.NORMAL);
            info.setBid(100L);
            info.setPlanId(planId);
            info.setUserId(USER_ID);
            info.setPriority(0);

            infos.add(info);
        }


        return infos;
    }

    private List<IdeaInfo> genIdea(Integer unitId, int tot) {
        List<IdeaInfo> infos = new ArrayList<>(tot);
        String[] urls = new String[] {
            "http://www.gae.com/showMonitor.gif?sid=a"
        };

        for (int ix = 0; ix < tot; ++ix) {
            IdeaInfo info = new IdeaInfo();
            info.setShowUrls(urls);
            info.setH(1080);
            info.setW(1920);
            info.setIdeaId(Integer.valueOf(RandomUtils.nextInt(1, 10000)).toString());
            info.setLandUrl("http://landding-url");
            info.setMaterialType(1);
            info.setStatus(IdeaStatus.PASS);

            infos.add(info);
        }

        return infos;
    }
}
