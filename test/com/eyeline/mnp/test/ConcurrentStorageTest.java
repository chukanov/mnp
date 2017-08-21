package com.eyeline.mnp.test;

import com.eyeline.mnp.ConcurrentStorage;
import com.eyeline.mnp.Storage;

/**
 * @author Chukanov
 */
public class ConcurrentStorageTest extends BaseStorageTest {
    @Override
    protected Storage build() {
        return new ConcurrentStorage(super.build());
    }
}
