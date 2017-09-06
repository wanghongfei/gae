package org.fh.gae.query.index.filter;

import org.fh.gae.query.index.plan.PlanInfo;
import org.fh.gae.query.index.plan.PlanStatus;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;

@Component
@DependsOn("filterTable")
public class PlanFilter implements GaeFilter<PlanInfo> {
    private static int TIME_BIT_LEN = 24 * 7;

    @PostConstruct
    private void init() {
        FilterTable.register(PlanInfo.class, this);
    }

    @Override
    public void filter(Collection<PlanInfo> elems) {
        traverse(elems, plan -> plan.getStauts() == PlanStatus.NORMAL && isTimeBitFit(plan.getTimeBit()));
    }

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
