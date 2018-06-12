package org.fh.gae.query.index.filter;

import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.memory.plan.PlanInfo;
import org.fh.gae.query.index.memory.plan.PlanStatus;
import org.fh.gae.query.profile.AudienceProfile;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;

/**
 * 推广计划过虑器
 */
@Component
@DependsOn("filterTable")
public class PlanFilter implements GaeFilter<PlanInfo> {
    private static int TIME_BIT_LEN = 24 * 7;

    @PostConstruct
    private void init() {
        FilterTable.register(PlanInfo.class, this);
    }

    @Override
    public void filter(Collection<PlanInfo> elems, RequestInfo request, AudienceProfile profile) {
        traverse(elems, plan -> plan.getStauts() == PlanStatus.NORMAL && isTimeBitFit(plan.getTimeBit()));
    }

    /**
     * 判断当前时间是否符合投放计划中选择的时间段;
     *
     * timeBit是168位二进制数转为10进制数的值, 168位中的每一位对应于周1到周7每小时的时间段，如,
     * 第1位表示周一0~1点是否投放广告, 为0表示不投, 1表示投; 第25位表示周二0~1点是否投放广告
     *
     * @param timeBit
     * @return
     */
    public boolean isTimeBitFit(String timeBit) {
        Calendar calendar = Calendar.getInstance();

        // 10进制转2进制
        BigInteger num = new BigInteger(timeBit);
        String binBit = num.toString(2);

        // 计算当前时间在bit中的位置
        // 0表示周日
        int weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (0 == weekday) {
            weekday = 7;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int pos = (weekday - 1) * 24 + hour;

        char bit = binBit.charAt(pos);

        return bit == '1';
    }
}
