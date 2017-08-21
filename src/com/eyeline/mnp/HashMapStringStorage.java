package com.eyeline.mnp;

import com.eyeline.mnp.mask.Mask;

import java.util.HashMap;
import java.util.Map;
/**
 * Mno storage based on HashMap<String,Mno>
 * @author Chukanov
 */
public class HashMapStringStorage implements Storage {
    private Map<String, Mno> storage = new HashMap<>();

    @Override
    public Mno lookup(String subscriber) {
        if (subscriber == null) return null;
        Mno result = storage.get(subscriber);
        if (result!=null) {
            return result;
        }
        StringBuilder mask = new StringBuilder(subscriber);
        for (int i=subscriber.length()-1; i>=0; i--) {
            mask.replace(i, i+1, Character.toString(Mask.WILDCARD));
            result = storage.get(mask.toString());
            if (result!=null) return result;
        }
        return null;
    }

    @Override
    public Mno put(Mask mask, Mno mno) {
        if (mask == null || mno == null) return null;
        return storage.put(mask.toString(), mno);
    }

    @Override
    public Mno remove(Mask mask) {
        if (mask == null) return null;
        return storage.remove(mask.toString());
    }

    @Override
    public void clear() {
        storage = new HashMap<>();
    }
}
