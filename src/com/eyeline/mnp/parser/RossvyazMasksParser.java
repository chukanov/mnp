package com.eyeline.mnp.parser;

import com.eyeline.mnp.mask.Mask;
import com.eyeline.mnp.mask.MasksUtils;
import com.eyeline.utils.BlankRunnable;
import com.eyeline.utils.DirectoryWatchDog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Chukanov
 */
public class RossvyazMasksParser extends DirectoryWatchDog implements MasksParser, MasksParser.WatchDog {

    private String countryCode = "7";
    private String country = "ru";
    private char delimeter = ';';

    private Charset charset = Charset.forName("WINDOWS-1251");
    private int skipLines = 1;

    private Path rossvyazFile;

    public RossvyazMasksParser(Path rossvyazFile) {
        this.rossvyazFile = rossvyazFile;
        if (Files.isDirectory(rossvyazFile)) {
            throw new IllegalArgumentException("Path "+ rossvyazFile +" is a directory");
        }
    }

    private String appendNull(String s, int length) {
        while (s.length() < length) {
            s = "0" + s;
        }
        return s;
    }

    @Override
    public void parse(Consumer<MnoInfo> consumer) throws Exception {
        try(Stream<String> linesStream = Files.lines(rossvyazFile, charset)) {
            linesStream.
                skip(skipLines).
                forEach(line -> {
                    String[] data = line.split(Character.toString(delimeter));
                    if (data.length!=6) return;
                    String maskPrefix = data[0].trim();
                    String maskMin = countryCode + maskPrefix + appendNull(data[1].trim(), 7);
                    String maskMax = countryCode + maskPrefix + appendNull(data[2].trim(), 7);
                    String maskCapability = data[3].trim();
                    String mnoName = data[4].trim();
                    String mnoRegion = data[5].trim();
                    Set<Mask> masks = MasksUtils.buildMasksByRange(maskMin, maskMax, Integer.valueOf(maskCapability));
                    MnoInfo mno = new MnoInfo(mnoName, mnoRegion, country, masks);
                    consumer.accept(mno);
                });
        }
    }

    @Override
    protected void processChangedFile(Path filePath) throws Exception {
        if (Files.isSameFile(rossvyazFile, filePath)) {
            onUpdate.run();
        }
    }

    @Override
    protected Path getWatchDir() {
        return rossvyazFile.normalize().getParent();
    }

    private Runnable onUpdate = new BlankRunnable();

    @Override
    public void watch(Runnable onUpdate) throws IOException {
        this.onUpdate = onUpdate;
        start();
    }
}
