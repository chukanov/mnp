package com.eyeline.mnp.example;

import com.eyeline.mnp.Builder;
import com.eyeline.mnp.Mno;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.parser.CustomMasksParser;
import com.eyeline.mnp.parser.RossvyazMasksParser;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        Storage storage = Builder.
                builder().
                add(new RossvyazMasksParser(Paths.get("config/rossvyaz/Kody_DEF-9kh.csv"))).
                add(new CustomMasksParser(Paths.get("config/mnos.xml"))).
                //add(new ZniisMnpParser(Paths.get("config/zniis/"))).
                idTitle( Paths.get("config/filters/titles.xml")).
                idRegion(Paths.get("config/filters/areas.xml")).
                build();
        Mno mno = storage.lookup("79139367911");
        System.out.println(mno);
    }
}
