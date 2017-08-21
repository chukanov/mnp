package com.eyeline.mnp.test;

import com.eyeline.mnp.mask.Mask;
import com.eyeline.mnp.mask.MasksUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chukanov
 */
public class MaskUtilsTest {
    @Test
    public void test(){
        Set<Mask> result = MasksUtils.buildMasksByRange("79000000000", 1);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(new Mask("79000000000")));

        result = MasksUtils.buildMasksByRange("79000000000", 10);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(new Mask("7900000000?")));

        result = MasksUtils.buildMasksByRange("79000000000", "79000000099");
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(new Mask("790000000??")));

        result = MasksUtils.buildMasksByRange("99109", "99299");
        Set<Mask> ideal = new HashSet<>(
                Arrays.asList(
                        new Mask("99109"),
                        new Mask("9911?"),
                        new Mask("9912?"),
                        new Mask("9913?"),
                        new Mask("9914?"),
                        new Mask("9915?"),
                        new Mask("9916?"),
                        new Mask("9917?"),
                        new Mask("9918?"),
                        new Mask("9919?"),
                        new Mask("992??")
                )
        );
        Assert.assertEquals(ideal, result);

        result = MasksUtils.buildMasksByRange("99000", 101);
        ideal = new HashSet<>(
                Arrays.asList(
                        new Mask("990??"),
                        new Mask("99100")
                )
        );
        Assert.assertEquals(ideal, result);

        result = MasksUtils.buildMasksByRange("1000", "1999");
        ideal = new HashSet<>(
                Arrays.asList(
                        new Mask("1???")
                )
        );
        Assert.assertEquals(ideal, result);

        result = MasksUtils.buildMasksByRange("8809", 1191);
        ideal = new HashSet<>(
                Arrays.asList(
                        new Mask("8809"),
                        new Mask("881?"),
                        new Mask("882?"),
                        new Mask("883?"),
                        new Mask("884?"),
                        new Mask("885?"),
                        new Mask("886?"),
                        new Mask("887?"),
                        new Mask("888?"),
                        new Mask("889?"),
                        new Mask("89??"),
                        new Mask("9???")
                )
        );
        Assert.assertEquals(ideal, result);

        result = MasksUtils.buildMasksByRange("001", "999");
        ideal = new HashSet<>(
                Arrays.asList(
                        new Mask("001"),
                        new Mask("002"),
                        new Mask("003"),
                        new Mask("004"),
                        new Mask("005"),
                        new Mask("006"),
                        new Mask("007"),
                        new Mask("008"),
                        new Mask("009"),
                        new Mask("01?"),
                        new Mask("02?"),
                        new Mask("03?"),
                        new Mask("04?"),
                        new Mask("05?"),
                        new Mask("06?"),
                        new Mask("07?"),
                        new Mask("08?"),
                        new Mask("09?"),
                        new Mask("1??"),
                        new Mask("2??"),
                        new Mask("3??"),
                        new Mask("4??"),
                        new Mask("5??"),
                        new Mask("6??"),
                        new Mask("7??"),
                        new Mask("8??"),
                        new Mask("9??")
                )
        );
        Assert.assertEquals(ideal, result);
    }

    @Test
    public void testFilter(){
        String input = "?0lo10o936u?";
        Assert.assertEquals("?010936?",MasksUtils.filter(input));
    }

}
