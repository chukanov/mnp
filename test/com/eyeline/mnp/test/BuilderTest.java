package com.eyeline.mnp.test;

import com.eyeline.mnp.Builder;
import com.eyeline.mnp.Mno;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.parser.MasksParser;
import com.eyeline.mnp.parser.MnpParser;
import com.eyeline.mnp.parser.RossvyazMasksParser;
import com.eyeline.mnp.parser.ZniisMnpParser;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author Chukanov
 */
public class BuilderTest {
    private Mno mno1 = new Mno("ru.mts.moscow", "ru", "mts", "moscow");
    private Mno mno2 = new Mno("ru.tele2.novosibirsk", "ru", "tele2", "novosibirsk");
    private Mno mno3 = new Mno("ru.beeline.s_peter", "ru", "beeline", "s_peter");

    private Path testDir = Paths.get("./test/config/");

    private Builder builder;

    protected Path writeMasks(List<String> testData) throws IOException {
        Path configFile = testDir.resolve("rossvyaz/test_masks.csv");
        Files.createDirectories(configFile.getParent());
        Files.write(configFile, testData);
        return configFile;
    }

    protected MasksParser buildMasks(List<String> testData) throws IOException {
        return new RossvyazMasksParser(writeMasks(testData));
    }

    protected MnpParser buildMnp() throws IOException {
        Path configDir = testDir.resolve("zniis");
        Path allNewNumbers = configDir.resolve(ZniisMnpParser.FILENAME_ALL);
        Files.createDirectories(allNewNumbers.getParent());
        Files.write(allNewNumbers, Arrays.asList(
                "Number,OrgName,RowCount",
                "9000000000,"+mno2.getTitle()+",13",// mts -> tele2
                "9010000000,"+mno1.getTitle(),      // tele2 -> mts
                "9000000001,"+mno2.getTitle(),      // mts -> tele2
                "9010000001,"+mno1.getTitle(),      // tele2 -> mts
                "9020000001,"+mno1.getTitle()       // beeline -> mts
        ));
        Files.write(configDir.resolve(ZniisMnpParser.FILENAME_LASTEST), Arrays.asList(
                "Number,OrgName,RowCount",
                "9000000005,"+mno3.getTitle(), //mts -> beeline
                "9010000006,"+mno1.getTitle(), //tele2 -> mts
                "9000000006,"+mno2.getTitle(), //mts -> tele2
                "9010000005,"+mno3.getTitle()  //tele2 -> beeline
        ));
        Files.write(configDir.resolve(ZniisMnpParser.FILENAME_RETURNED), Arrays.asList(
                "Number,OrgName,RowCount",
                "9000000006,mno1", // return to mts
                "9010000001,mno2", // return to tele2
                "9020000001,mno1"  // return to beeline
        ));
        return new ZniisMnpParser(configDir);
    }

    @Before
    public void prepare() {
        builder = Builder.builder();
    }

    @After
    public void fin() throws IOException {
        for(Path p : Files.walk(testDir).
                sorted((a, b) -> b.compareTo(a)). // reverse; files before dirs
                toArray(Path[]::new))
        {
            Files.delete(p);
        }
    }

    @Test
    public void testMasksStorage() throws Exception {
        List<String> testData = Arrays.asList(
                "АВС/ DEF;От;До;Емкость;Оператор;Регион",
                "900\t;\t0000000\t;\t0000009\t;\t10\t;\t"+mno1.getTitle()+"\t;\t"+mno1.getArea(),
                "901\t;\t0000000\t;\t0000009\t;\t10\t;\t"+mno2.getTitle()+"\t;\t"+mno2.getArea(),
                "902\t;\t0000001\t;\t0000001\t;\t1\t;\t" +mno3.getTitle()+"\t;\t"+mno3.getArea()
        );
        builder.add(buildMasks(testData));
        Storage storage = builder.build();
        for (int i=0; i<10; i++) {
            Mno mno = storage.lookup("7900000000"+i);
            Assert.assertEquals(mno1, mno);
            Assert.assertEquals(mno1.getCountry(), mno.getCountry());
            Assert.assertEquals(mno1.getArea(), mno.getArea());
            Assert.assertEquals(mno1.getTitle(), mno.getTitle());
        }
        Assert.assertNull(storage.lookup("79000000010"));
        for (int i=0; i<10; i++) {
            Mno mno = storage.lookup("7901000000"+i);
            Assert.assertEquals(mno2, mno);
            Assert.assertEquals(mno2.getCountry(), mno.getCountry());
            Assert.assertEquals(mno2.getArea(), mno.getArea());
            Assert.assertEquals(mno2.getTitle(), mno.getTitle());
        }
        Assert.assertNull(storage.lookup("79010000010"));
        Assert.assertEquals(mno3, storage.lookup("79020000001"));
    }

    @Test
    public void testMnpStorage() throws Exception {
        List<String> testData = Arrays.asList(
                "АВС/ DEF;От;До;Емкость;Оператор;Регион",
                "900\t;\t0000000\t;\t0000009\t;\t10\t;\t"+mno1.getTitle()+"\t;\t"+mno1.getArea(),
                "901\t;\t0000000\t;\t0000009\t;\t10\t;\t"+mno2.getTitle()+"\t;\t"+mno2.getArea(),
                "902\t;\t0000001\t;\t0000001\t;\t1\t;\t" +mno3.getTitle()+"\t;\t"+mno3.getArea()
        );
        builder.add(buildMasks(testData));
        builder.add(buildMnp());
        Storage storage = builder.build();
        for (int i=0; i<10; i++) {
            String subscriber = "7900000000"+i;
            Mno mno = storage.lookup(subscriber);
            Assert.assertEquals(mno1.getArea(), mno.getArea());
            switch (subscriber) {
                case "79000000000":
                case "79000000001":
                case "79010000001":
                    Assert.assertEquals(subscriber, mno2.getTitle(), mno.getTitle());
                    break;
                case "79000000005":
                    Assert.assertEquals(subscriber, mno3.getTitle(), mno.getTitle());
                    break;
                default:
                    Assert.assertEquals(subscriber, mno1.getTitle(), mno.getTitle());
            }
        }
        for (int i=0; i<10; i++) {
            String subscriber = "7901000000"+i;
            Mno mno = storage.lookup(subscriber);
            Assert.assertEquals(mno2.getArea(), mno.getArea());
            switch (subscriber) {
                case "79010000000":
                    Assert.assertEquals(mno1.getTitle(), mno.getTitle());
                    break;
                case "79010000005":
                    Assert.assertEquals(mno3.getTitle(), mno.getTitle());
                    break;
                case "79010000006":
                    Assert.assertEquals(mno1.getTitle(), mno.getTitle());
                    break;
                default:
                    Assert.assertEquals(mno2.getTitle(), mno.getTitle());
            }
        }
        Assert.assertEquals(mno3, storage.lookup("79020000001"));
    }
}
