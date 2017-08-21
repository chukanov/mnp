package com.eyeline.mnp;

import com.eyeline.mnp.mask.Mask;

/**
 * Represents MNO storage with ability to retrieve MNP by phone number
 * @author Chukanov
 */
public interface Storage {
    /**
     * Lookup MNO by phone number
     * @param subscriber phone number
     * @return mno if exists, otherwise null
     */
    public Mno lookup(String subscriber);

    /**
     * Adds a new mask to mno in storage
     * but only if mask is not exists
     * @param mask new mask
     * @param mno mno
     * @return Mno – old value if present
     */
    public Mno put(Mask mask, Mno mno);

    /**
     * remove mask from the storage
     * @param mask mask
     * @return old value
     */
    public Mno remove(Mask mask);

    /**
     * clear the storage
     */
    public void clear();
}
