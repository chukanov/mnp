package com.eyeline.mnp;

import com.eyeline.mnp.mask.Mask;

import java.util.HashMap;
import java.util.Map;

/**
 * Mno storage based on HashMap<Mask,Mno>
 * @author Chukanov
 */
public class HashMapStorage implements Storage {
    private Map<Mask, Mno> storage = new HashMap<>();

    @Override
    public Mno lookup(String subscriber) {
        if (subscriber == null) return null;
        Mask mask = Mask.parse(subscriber);
        while (mask!=null) {
            Mno result = storage.get(mask);
            if (result!=null) {
                return result;
            }
            mask = mask.getWider();
        }
        return null;
    }

    @Override
    public Mno put(Mask mask, Mno mno) {
        if (mask == null || mno == null) return null;
        return storage.put(mask, mno);
    }

    @Override
    public Mno remove(Mask mask) {
        if (mask == null) return null;
        return storage.remove(mask);
    }

    @Override
    public void clear() {
        storage = new HashMap<>();
    }
}
