package com.eyeline.mnp.example;

import com.eyeline.mnp.Builder;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.parser.CustomMasksParser;
import com.eyeline.mnp.parser.RossvyazMasksParser;
import com.eyeline.mnp.parser.ZniisMnpParser;

import java.nio.file.Paths;
import java.util.Collection;

public class Main {

    public static void main(String[] args) throws Exception {
        Storage storage = Builder.
                builder().
                add(new RossvyazMasksParser(Paths.get("config/rossvyaz/ru.backup.2017-08-09-11-45.csv"))).
                add(new CustomMasksParser(Paths.get("config/mnos.xml"))).
                add(new ZniisMnpParser(Paths.get("config/zniils/"))).
                idTitle( Paths.get("config/idfilters/titles.xml")).
                idRegion(Paths.get("config/idfilters/areas.xml")).
                build();
        System.out.println(storage.lookup("79139367911"));
        System.out.println(storage.lookup("79000000001"));
        System.out.println(storage.lookup("79005555552"));
        System.out.println(storage.lookup("19005555552"));
    }
}
