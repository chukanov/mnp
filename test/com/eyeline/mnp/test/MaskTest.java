package com.eyeline.mnp.test;

import com.eyeline.mnp.mask.Mask;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Chukanov
 */
public class MaskTest {
    @Test
    public void testMatch() {
        Mask mask = new Mask("791393679??");
        assertTrue(mask.match("79139367911"));
        for (int i=0; i<10; i++){
            for (int j=0; j<10; j++){
                assertTrue(mask.match("791393679"+i+j));
            }
        }
        assertFalse(mask.match("79139367911"+1));
        assertFalse(mask.match("79139367711"));
        assertFalse(mask.match("7913936791"));
    }

    @Test
    public void testLenght() {
        String maskStr = "791393679??";
        Mask mask = new Mask(maskStr);
        assertEquals(maskStr.length(), mask.length());
    }

    @Test
    public void testParse() {
        boolean allOk;
        for (char c=Character.MIN_VALUE; c<Character.MAX_VALUE; c++){
            try {
                if (c=='٠') {
                    allOk = true;
                }
                Mask.parse("7913936791"+c);
                allOk = true;
            } catch (Throwable e) {
                allOk = false;
//                Mask.parse("7913936791"+c);
            }
            if (c == Mask.WILDCARD || (c>='0' && c<='9')) {
                assertTrue("Character: '"+c+"'", allOk);
            } else {
                assertFalse("Character: '"+c+"'",allOk);
            }
        }
        try {
            Mask.parse("791393679X1");
            allOk = true;
        } catch (Throwable e) {
            allOk = false;
        }
        assertFalse(allOk);
    }
    @Test
    public void creationTest() {
        try{
            new Mask(null);
            fail();
        } catch (NullPointerException npe) {
            //all ok;
        }
        try{
            Mask.parse(null);
        } catch (Throwable npe) {
            fail();
        }
        String mask = "";
        for (int i=0; i<Mask.MAX_LENGTH; i++) {
            mask +=(int)(Math.random()*10);
        }
        try{
            Mask.parse(mask);
        } catch (Throwable npe) {
            fail();
        }
        mask += (int)(Math.random()*10);
        try{
            Mask.parse(mask);
            fail();
        } catch (Throwable npe) {
            //all ok
        }
    }

    @Test
    public void numberConversionTest() {
        for (char c=Character.MIN_VALUE; c<Character.MAX_VALUE; c++){
            try {
                int i = Mask.digit(c);
                if (i<0 || i>9) {
                    fail();
                }
            } catch (Throwable t) {
                if (c >='0' && c<='9') {
                    fail();
                }
            }
        }
    }

    @Test
    public void testEquals(){
        Mask m1 = new Mask("1234");
        Mask m2 = new Mask(m1.toString());

        assertEquals(m1, m2);
        m2 = new Mask(m1.toString()+"1");

        assertNotEquals(m1, m2);
    }
}
