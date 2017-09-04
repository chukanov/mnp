package com.eyeline.mnp;

import com.eyeline.mnp.mask.Mask;
import com.eyeline.mnp.mask.MaskTrieMap;

/**
 * @author Chukanov
 */
public class MasksStorage implements Storage{

    private MaskTrieMap<Mno> storage = new MaskTrieMap<>();

    @Override
    public Mno lookup(String phone) {
        if (phone == null) return null;
        return storage.lookup(Mask.parse(phone));
    }

    public Mno put(Mask mask, Mno mno) {
        if (mask == null || mno == null) return null;
        return storage.set(mask, mno);
    }

    @Override
    public Mno remove(Mask mask) {
        if (mask == null) return null;
        return storage.remove(mask);
    }

    @Override
    public void clear() {
        storage = new MaskTrieMap<>();
    }
}
