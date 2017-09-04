package com.eyeline.mnp.example;

import com.eyeline.mnp.Builder;
import com.eyeline.mnp.Mno;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.parser.CustomMasksParser;
import com.eyeline.mnp.parser.RossvyazMasksParser;
import com.eyeline.mnp.parser.ZniisMnpParser;

import java.io.IOException;
import java.nio.file.Paths;

public class SpeedTest {
    public static void main(String[] args) throws Exception {
        Storage storage = Builder.
                builder().
                add(new RossvyazMasksParser(Paths.get("config/rossvyaz/Kody_DEF-9kh.csv"))).
                add(new CustomMasksParser(Paths.get("config/mnos.xml"))).
                add(new ZniisMnpParser(Paths.get("config/zniis/"))).
                        idTitle( Paths.get("config/filters/titles.xml")).
                        idRegion(Paths.get("config/filters/areas.xml")).
                        build();
        System.out.println("Starting");
        long total = 0;
        long n = 10000000;
        for (int i=0; i<n; i++) {
            String number = genNumber();
            long startTime = System.nanoTime();
            Mno mno = storage.lookup(number);
            long endTime = System.nanoTime();
            long t = endTime - startTime;
            total = total + t;
        }
        System.out.println("Total time: "+total+" ns");
        System.out.println("Avg time on lookup: "+total/n+" ns");

    }

    private static String genNumber() {
        long x = (long) (Math.random()*100_000_0000);
        long n = 7_900_000_0000L + x;
        return Long.toString(n);
    }
}
