package com.eyeline.mnp.test;

import com.eyeline.mnp.HashMapStorage;
import com.eyeline.mnp.Storage;

/**
 * @author Chukanov
 */
public class HashMapStorageTest  extends BaseStorageTest {
    @Override
    protected Storage build() {
        return new HashMapStorage();
    }
}
