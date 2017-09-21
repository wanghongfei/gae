package org.fh.gae.query;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class Picker {
    public <T> T pickOne(List<T> dataList) {
        int size = dataList.size();

        Random random = new Random();
        int ix = random.nextInt(size);

        return dataList.get(ix);
    }
}
