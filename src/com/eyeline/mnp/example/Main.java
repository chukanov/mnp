package com.eyeline.mnp.example;

import com.eyeline.mnp.Builder;
import com.eyeline.mnp.Mno;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.mask.Mask;
import com.eyeline.mnp.mask.MasksUtils;
import com.eyeline.mnp.parser.CustomMasksParser;
import com.eyeline.mnp.parser.RossvyazMasksParser;
import com.eyeline.mnp.parser.ZniisMnpParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        Storage storage = Builder.
                builder().
                add(new RossvyazMasksParser(Paths.get("config/rossvyaz/Kody_DEF-9kh.csv"))).
                add(new CustomMasksParser(Paths.get("config/mnos.xml"))).
                add(new ZniisMnpParser(Paths.get("config/zniis/"))).
                idTitle( Paths.get("config/filters/titles.xml")).
                idRegion(Paths.get("config/filters/areas.xml")).
                build();
        Path subscribers = Paths.get("/Users/jeck/145_receivers.txt");
        System.out.println("========START==============");
        Path out = Paths.get("/Users/jeck/145_tele2_receivers.txt");
        BufferedWriter writer = Files.newBufferedWriter(out);
        Files.lines(subscribers).forEach(
                subscriber -> {
                    String s = MasksUtils.filter(subscriber);
                    if (s!=null && Mask.isValid(s)) {
                        Mno mno = storage.lookup(s);
                        if (mno==null) return;
                        if (mno.getId().startsWith("ru.tele2")) {
                            try {
                                writer.write(s+"\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        writer.flush();
        writer.close();
        System.out.println("=========FINISH=============");
    }
}
