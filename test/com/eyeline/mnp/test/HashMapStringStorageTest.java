package com.eyeline.mnp.test;

import com.eyeline.mnp.HashMapStringStorage;
import com.eyeline.mnp.Storage;

/**
 * @author Chukanov
 */
public class HashMapStringStorageTest extends BaseStorageTest {
    @Override
    protected Storage build() {
        return new HashMapStringStorage();
    }
}
