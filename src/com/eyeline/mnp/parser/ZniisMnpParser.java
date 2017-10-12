package com.eyeline.mnp.parser;

import com.eyeline.utils.BlankRunnable;
import com.eyeline.utils.DirectoryWatchDog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Chukanov
 */
public class ZniisMnpParser extends DirectoryWatchDog implements MnpParser, MnpParser.WatchDog {
    public static final String FILENAME_ALL = "Port_All_New.csv";
    public static final String FILENAME_LASTEST = "Port_Hour_New.csv";
    public static final String FILENAME_RETURNED = "Port_Hour_Returned.csv";

    private String countryCode = "7";
    private char delimeter = ',';
    private int skipLines = 1;
    private Charset charset = Charset.forName("WINDOWS-1251");

    private Path dir;

    public ZniisMnpParser(Path dir) {
        this.dir = dir;
    }

    protected void parse(Path file, Consumer<MnpParser.Number> consumer) throws IOException {
        try(Stream<String> linesStream = Files.lines(file, charset)) {
            linesStream.
                skip(skipLines).
                forEach(line -> {
                    String[] data = line.split(Character.toString(delimeter));
                    String subscriber = countryCode + data[0].trim();
                    String title = data[1].trim();
                    MnpParser.Number number = new MnpParser.Number(subscriber, title);
                    try {
                        consumer.accept(number);
                    } catch (Throwable t) {
                        System.err.print("Error at file: "+file+", line: "+line);
                        t.printStackTrace();
                    }
                });
        }
    }

    @Override
    public void parseAllPorted(Consumer<Number> consumer) throws Exception {
        this.parse(dir.resolve(FILENAME_ALL), consumer);
    }

    @Override
    public void parseLastestPorted(Consumer<Number> consumer) throws Exception {
        this.parse(dir.resolve(FILENAME_LASTEST), consumer);
    }

    @Override
    public void parseReturned(Consumer<Number> consumer) throws Exception {
        this.parse(dir.resolve(FILENAME_RETURNED), consumer);
    }


    private Runnable doOnAllUpdates = new BlankRunnable();
    private Runnable doOnlastUpdates = new BlankRunnable();
    private Runnable doOnreturnedUpdates = new BlankRunnable();

    public void watch(Runnable onAllUpdates,
                      Runnable onLastUpdates,
                      Runnable onReturnedUpdates) throws IOException {
        this.doOnAllUpdates = onAllUpdates;
        this.doOnlastUpdates = onLastUpdates;
        this.doOnreturnedUpdates = onReturnedUpdates;
        start();
    }

    @Override
    protected void processChangedFile(Path filePath) throws Exception {
        switch (filePath.getFileName().toString()) {
            case FILENAME_ALL: doOnAllUpdates.run(); break;
            case FILENAME_LASTEST: doOnlastUpdates.run(); break;
            case FILENAME_RETURNED: doOnreturnedUpdates.run(); break;
        }
    }

    @Override
    protected Path getWatchDir() {
        return dir;
    }
}
