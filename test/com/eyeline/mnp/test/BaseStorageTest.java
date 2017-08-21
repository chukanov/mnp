package com.eyeline.mnp.test;

import com.eyeline.mnp.Mno;
import com.eyeline.mnp.MasksStorage;
import com.eyeline.mnp.Storage;
import com.eyeline.mnp.mask.Mask;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Chukanov
 */
public class BaseStorageTest {
    private String subscriber1 = "79100000000";
    private String subscriber2 = "79200000000";
    private String subscriber3 = "79300000000";
    private Mno mno1 = new Mno("ru.mts.moscow", "ru", "mts", "moscow");
    private Mno mno2 = new Mno("ru.tele2.novosibirsk", "ru", "tele2", "novosibirsk");
    private Mno mno3 = new Mno("ru.beeline.s_peter", "ru", "beeline", "s_peter");

    private Storage storage;

    protected Storage build() {
        return new MasksStorage();
    }

    @Before
    public void prepare() {
        storage = build();
        storage.put(new Mask(subscriber1), mno1);
        storage.put(new Mask(subscriber2), mno2);
    }

    @Test
    public void testAdd(){
        assertEquals(mno1, storage.put(new Mask(subscriber1), mno1));
        assertEquals(mno2, storage.put(new Mask(subscriber2), mno2));

        storage.clear();
        assertNull(storage.put(new Mask(subscriber1), mno1));
        assertNull(storage.put(new Mask(subscriber2), mno2));

        assertNull(storage.put(new Mask(subscriber1), null));
        assertNull(storage.put(null, mno1));
        assertNull(storage.put(null, null));

        assertNull(storage.put(new Mask(subscriber3), mno1));

    }

    @Test
    public void testClear(){
        storage.clear();
        assertNull(storage.lookup(subscriber1));
        assertNull(storage.lookup(subscriber2));
    }

    @Test
    public void testRemove() {
        assertEquals(mno1, storage.remove(new Mask(subscriber1)));
        assertNull(storage.remove(new Mask(subscriber1)));

        assertNull(storage.put(new Mask(subscriber1), mno1));

        assertNull(storage.remove(new Mask(subscriber3)));
        assertNull(storage.remove(null));
    }

    @Test
    public void testLookup(){
        assertEquals(mno1, storage.lookup(subscriber1));
        assertEquals(mno2, storage.lookup(subscriber2));

        storage.clear();

        storage.put(new Mask("791000000??"), mno1);
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                assertTrue(mno1 == storage.lookup("791000000"+i+j));
            }
        }
        storage.put(new Mask("7910000000?"), mno2);
        storage.put(new Mask("79100000000"), mno3);
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                if (i==0) {
                    if (j==0) {
                        assertTrue(mno3 == storage.lookup("79100000000"));
                    } else {
                        assertTrue(mno2 == storage.lookup("7910000000"+j));
                    }
                } else {
                    assertTrue(mno1 == storage.lookup("791000000"+i+j));
                }
            }
        }

        storage.clear();
        storage.put(new Mask("???????????"),mno1);
        assertEquals(mno1, storage.lookup(subscriber1));
        assertEquals(mno1, storage.lookup(subscriber2));
        assertEquals(mno1, storage.lookup(subscriber3));
        assertNull(storage.lookup(subscriber3+"1"));
        assertNull(storage.lookup(subscriber3.substring(0, subscriber3.length()-1)));
        try{
            storage.lookup("7900???0000");
            fail();
        } catch (Throwable t){
            //all ok
        }
    }


    @Test
    public void testLookup2(){
        storage.clear();
        storage.put(new Mask("7900000000?"), mno1);
        storage.put(new Mask("79000000001"), mno2);
        storage.put(new Mask("79000000002"), mno2);
        storage.put(new Mask("79000000003"), mno2);
        assertEquals(mno1, storage.lookup("79000000000"));
        assertEquals(mno2, storage.lookup("79000000001"));
        assertEquals(mno2, storage.lookup("79000000002"));
        assertEquals(mno2, storage.lookup("79000000003"));
        for (int i=4; i<10; i++) {
            assertEquals(mno1, storage.lookup("7900000000"+i));
        }
    }
}
