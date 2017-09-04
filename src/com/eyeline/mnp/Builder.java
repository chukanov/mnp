package com.eyeline.mnp;

import com.eyeline.mnp.mask.Mask;
import com.eyeline.mnp.parser.MasksParser;
import com.eyeline.mnp.parser.MnpParser;
import com.eyeline.utils.Translit;
import com.eyeline.utils.filter.Filter;
import com.eyeline.utils.filter.PatternFilter;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
/**
 * Bulder to help create and configurate storage
 * @author Chukanov
 */
public class Builder {
    private static final Logger log = Logger.getLogger(Builder.class);


    private List<MasksParser> masksParsers = new ArrayList<>();
    private List<MnpParser> mnpParsers = new ArrayList<>();

    private Filter.Chain titleFilter = new Filter.Chain();
    private Filter.Chain regionFilter = new Filter.Chain();

    public static Builder builder() {
        return new Builder();
    }

    private ChainStorage storage = null;

    private ConcurrentStorage masksStorage = null;
    private ConcurrentStorage mnpStorage = null;

    private HashMap<String, Mno> mnoCache = new HashMap<>();

    private UpdatePolicy updatePolicy = UpdatePolicy.BUILD_AND_REPLACE;

    private Builder() {
    }

    public Builder add(MasksParser parser) {
        this.masksParsers.add(parser);
        return this;
    }

    public Builder add(MnpParser parser) {
        this.mnpParsers.add(parser);
        return this;
    }

    public Builder idTitle(Path filterConfig) throws IOException {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream(filterConfig.toFile())){
            config.loadFromXML(fis);
            this.titleFilter.add(PatternFilter.loadFromProperties(config));
        }
        return this;
    }

    public Builder idRegion(Path filterConfig) throws IOException {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream(filterConfig.toFile())){
            config.loadFromXML(fis);
            this.regionFilter.add(PatternFilter.loadFromProperties(config));
        }
        return this;
    }

    private Storage produceMNPStorage() {
        //return new HashMapStorage();
        return new MasksStorage();
    }

    public Storage build() throws Exception {
        if (storage!=null) {
            return storage;
        }
        masksStorage = new ConcurrentStorage(new MasksStorage());
        mnpStorage = new ConcurrentStorage(produceMNPStorage());
        storage = new ChainStorage(mnpStorage, masksStorage);
        this.processMasks(masksStorage);
        this.processMnpAllPorted(mnpStorage);
        this.processMnpLastPorted(mnpStorage);
        this.processMnpReturned(mnpStorage);
        if (updatePolicy!=UpdatePolicy.DISABLED) {
            for (MasksParser parser: masksParsers) {
                if (parser instanceof MasksParser.WatchDog) {
                    ((MasksParser.WatchDog) parser).watch(
                        () -> {
                            log.info("Masks update policy: "+updatePolicy);
                            switch (updatePolicy) { //todo rebuild MNP storage too
                                case CLEAR_AND_BUILD:
                                    masksStorage.clear();
                                    processMasks(masksStorage);
                                    break;
                                case BUILD_AND_REPLACE:
                                    Storage storage = new MasksStorage();
                                    processMasks(storage);
                                    this.masksStorage.setRealStorage(storage);
                            }
                        }
                    );
                }
            }
            for (MnpParser parser: mnpParsers) {
                if (parser instanceof MnpParser.WatchDog) {
                    ((MnpParser.WatchDog) parser).watch(
                            () -> {
                                log.info("Ported numbers update policy: "+updatePolicy);
                                switch (updatePolicy) {
                                    case CLEAR_AND_BUILD:
                                        mnpStorage.clear();
                                        processMnpAllPorted(mnpStorage);
                                        processMnpLastPorted(mnpStorage);
                                        processMnpReturned(mnpStorage);
                                        break;
                                    case BUILD_AND_REPLACE:
                                        Storage storage = produceMNPStorage();
                                        processMnpAllPorted(storage);
                                        processMnpLastPorted(storage);
                                        processMnpReturned(storage);
                                        this.mnpStorage.setRealStorage(storage);
                                }
                            },
                            () -> {
                                processMnpLastPorted(mnpStorage);
                            },
                            () -> {
                                processMnpReturned(mnpStorage);
                            }
                    );
                }
            }

        }
        return storage;
    }

    private void processMasks(Storage storage) {
        log.info("processing masks start");
        for (MasksParser parser: masksParsers) {
            try {
                parser.parse(info -> {
                    String id = id(info.getCountry(),
                            titleFilter.filter(info.getTitle()),
                            regionFilter.filter(info.getArea())
                    );
                    Mno mno = mnoCache.get(id);
                    if (mno == null) {
                        mno = new Mno(id, info.getCountry(), info.getTitle(), info.getArea());
                        mnoCache.put(id, mno);
                    }
                    for (Mask mask: info.getMasks()) {
                        storage.put(mask, mno);
                    }
                });
            } catch (Exception e) {
                log.warn("",e);
            }
        }
        log.info("processing masks over. count MNO in storage: "+mnoCache.size());
    }

    private Consumer<MnpParser.Number> produceAddMnpConsumer(Storage storage) {
        return number -> {
            Mno mno = masksStorage.lookup(number.getNumber());
            String id = id(mno.getCountry(), titleFilter.filter(number.getTitle()), regionFilter.filter(mno.getArea()));
            Mno newMno = mnoCache.get(id);
            if (newMno == null) {
                newMno = new Mno(id, mno.getCountry(), number.getTitle(), mno.getArea());
                mnoCache.put(id, newMno);
            }
            storage.put(Mask.parse(number.getNumber()), newMno);
        };
    }

    private void processMnpAllPorted(Storage storage) {
        log.info("processing mnp all ported start");
        Consumer<MnpParser.Number> addMnpNumberWorker = produceAddMnpConsumer(storage);
        for (MnpParser parser: mnpParsers) {
            try { parser.parseAllPorted(addMnpNumberWorker); } catch (Exception e) {log.warn("",e);}
        }
        log.info("processing mnp all over. count MNO in storage: "+mnoCache.size());
    }

    private void processMnpLastPorted(Storage storage) {
        log.info("processing mnp last ported start");
        Consumer<MnpParser.Number> addMnpNumberWorker = produceAddMnpConsumer(storage);
        for (MnpParser parser: mnpParsers) {
            try { parser.parseLastestPorted(addMnpNumberWorker);} catch (Exception e) {log.warn("",e);}
        }
        log.info("processing mnp last ported over. count MNO in storage: "+mnoCache.size());
    }

    private void processMnpReturned(Storage storage) {
        log.info("processing mnp returned start");
        Consumer<MnpParser.Number> removeMnpNumber = number -> {
            storage.remove(Mask.parse(number.getNumber()));
        };
        for (MnpParser parser: mnpParsers) {
            try { parser.parseReturned(removeMnpNumber);} catch (Exception e) { log.warn("",e); }
        }
        log.info("processing mnp returnred over. count MNO in storage: "+mnoCache.size());
    }

    public static String id(String country, String title, String region) {
        return Translit.cyr(country + "."+ title + "." + region);
    }

    /**
     * Type of autoupdate policy.
     * DISABLED – autoupdate disabled
     * BUILD_AND_REPLACE– if data changed, new storage will be created. After that original storage will be replaced.
     * It needs double memory amount.
     * CLEAR_AND_BUILD – if data changed, original storage will be cleared, and will build like a new one.
     */
    public static enum UpdatePolicy {
        DISABLED,
        BUILD_AND_REPLACE,
        CLEAR_AND_BUILD
    }
}
