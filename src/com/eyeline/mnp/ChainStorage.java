package com.eyeline.mnp;

import com.eyeline.mnp.mask.Mask;

/**
 * Chained storage
 * @author Chukanov
 */
public class ChainStorage implements Storage{
    private Storage[] storages;

    public ChainStorage(Storage... storages) {
        this.storages = storages;
    }

    @Override
    public Mno lookup(String subscriber) {
        for (Storage storage: storages){
            Mno mno = null;
            if(storage!=null) {
                mno = storage.lookup(subscriber);
            }
            if (mno!=null) return mno;
        }
        return null;
    }

    @Override
    public Mno put(Mask mask, Mno mno) {
        Mno result = null;
        for (Storage storage: storages) {
            if (storage!=null) {
                result = storage.put(mask, mno);
            }
        }
        return result;
    }

    @Override
    public Mno remove(Mask mask) {
        Mno mno = null;
        for (Storage storage: storages) {
            if (storage!=null) {
                mno = storage.remove(mask);
            }
        }
        return mno;
    }

    @Override
    public void clear() {
        for (Storage storage: storages) {
            if (storage!=null) {
                storage.clear();
            }
        }
    }
}
