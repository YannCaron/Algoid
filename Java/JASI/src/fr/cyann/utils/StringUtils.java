/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.utils;

/**
 *
 * @author caronyn
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }

        return sb.toString();
    }

    public static String repeat(char chr, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(chr);
        }

        return sb.toString();
    }

    public static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
