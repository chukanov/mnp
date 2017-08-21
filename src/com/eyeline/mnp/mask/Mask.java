package com.eyeline.mnp.mask;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Represents telephone number mask
 * where '?' is a wildcard symbol.
 * Maximum length of mask is 15 symbols
 * @author Chukanov
 */
public class Mask {
    public static final int MAX_LENGTH = 15;
    private static final String EMPTY_MASK = "";
    public static final char WILDCARD = '?';
    private static final char ZERO = '0';

    private byte[] data;

    public Mask(String mask) {
        this.data = mask.getBytes(Charset.forName("ASCII"));
    }

    private Mask(byte[] data) {
        this.data = data;
    }

    /**
     *
     * @param number telephone number mask
     * @return true – if number matches mask, false – if otherwise. if number is null - return false
     *
     */
    public boolean match(String number) {
        if (this.data.length == 0 || number == null) return false;
        if (number.length()!=data.length) return false;
        for (int i=0; i<number.length(); i++) {
            if (data[i] == WILDCARD) continue;
            if (data[i] != number.charAt(i)) return false;
        }
        return true;
    }

    /**
     * Return wider mask
     * 123 returns 12?
     * 12? returns 1??
     * 1?? returns ???
     * ??? returns null
     * @return Return wider mask
     */
    public Mask getWider() {
        int i;
        for (i=data.length-1; i>=0; i--) {
            if (data[i] != WILDCARD) {
                break;
            }
        }
        if (i < 0) {
            return null;
        } else {
            byte[] newData = new byte[data.length];
            System.arraycopy(data, 0, newData, 0, data.length);
            newData[i] = WILDCARD;
            return new Mask(newData);
        }
    }

    /**
     *
     * @return int lenght of the mask
     */
    public int length(){
        return data.length;
    }

    public char charAt(int position) {
        return (char) data[position];
    }

    /**
     *
     * @return string representation of mask
     */
    @Override
    public String toString() {
        return new String(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mask mask = (Mask) o;
        return Arrays.equals(data, mask.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    /**
     * constructs mask from string representation with validation
     * @param mask string representation fo mask
     * @return new mask
     * @throws IllegalArgumentException if mask has a bad format
     */
    public static Mask parse(String mask) {
        String m = mask;
        if (m == null) {
            m = EMPTY_MASK;
        }
        if (m.length() > MAX_LENGTH)
            throw new IllegalArgumentException("mask: "+mask+" is longer than limit "+ MAX_LENGTH + " (by E.164 standard)");
        Mask res = new Mask(m);
        byte[] data = res.data;
        if (m.indexOf(WILDCARD)!=-1) {
            boolean gotWildcard = false;
            for (byte c: data) {
                if (c == WILDCARD && !gotWildcard) {
                    gotWildcard = true;
                } else if (gotWildcard && c!=WILDCARD){
                    throw new IllegalArgumentException("mask: "+m+" is invalid ("+m+")");
                } else if (!gotWildcard) {
                    try {
                        digit((char) c);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException(e.getMessage()+" in mask: "+mask);
                    }
                }
            }
        } else {
            for (byte c: data) {
                if (!Character.isDigit(c)) {
                    throw new IllegalArgumentException("mask: "+m+" is contains invalid character ("+c+")");
                }
            }
        }
        return res;
    }

    /**
     * @param mask mask to check validity
     * @return true if valid, false - if not
     */
    public static boolean isValid(String mask) {
        try{
            parse(mask);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    /**
     *
     * @param c character from mask
     * @return digit represents current char in number
     * @throws IllegalArgumentException if char is out of supported range (0-9)
     */
    public static int digit(char c) {
        int i =  c - ZERO;
        if (i  < 0 || i > 9)
            throw new IllegalArgumentException("char " + c + " is out of supported range (0-9)");
        return i;
    }
}
