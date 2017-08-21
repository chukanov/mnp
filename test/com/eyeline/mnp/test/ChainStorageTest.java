package com.eyeline.mnp.test;

import com.eyeline.mnp.ChainStorage;
import com.eyeline.mnp.MasksStorage;
import com.eyeline.mnp.Storage;

/**
 * @author Chukanov
 */
public class ChainStorageTest extends BaseStorageTest {
    @Override
    protected Storage build() {
        return new ChainStorage(new MasksStorage(), new MasksStorage());
    }
}
