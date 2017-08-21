package com.eyeline.utils;

/**
 * @author Chukanov
 */
public class Translit {
    private final static String[] cyr = {
            "A", "B", "V", "G", "D", "E", "J", "Z", "I", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F",
            "H", "C", "Ch", "Sh", "Sch", "'", "Y", "'", "e", "u", "ia",
            "a", "b", "v", "g", "d", "e", "j", "z", "i", "i", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f",
            "h", "c", "ch", "sh", "sch", "'", "y", "'", "e", "yu", "ia"
    };


    private Translit(){}

    public static String cyr(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(2*s.length());
        for (char c: s.toCharArray()) {
            if ('А' <= c && c <= 'я') {
                int idx = c - 'А';
                sb.append(cyr[idx]);
            } else if (c == 'Ё') {
                sb.append('E');
            } else if (c == 'ё') {
                sb.append('e');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
