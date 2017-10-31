package org.fh.gae.test.other;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RegionQuery {
    /**
     * 查询地域id对应的城市名
     * @throws IOException
     */
    @Test
    public void testQueryRegionName() throws IOException {
        Map<Integer, String> regionMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream("data/reg.txt")) {
            InputStreamReader r = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(r);


            String line;
            while ( (line = reader.readLine()) != null ) {
                String[] terms = line.split("\t");
                Integer id = Integer.valueOf(terms[0]);
                String name = terms[1];

                regionMap.put(id, name);
            }


            System.out.println(regionMap.get(20));


        } catch (IOException e) {
            throw e;
        }
    }
}
