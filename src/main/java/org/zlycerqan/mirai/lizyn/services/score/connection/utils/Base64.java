package org.zlycerqan.mirai.lizyn.services.score.connection.utils;

public class Base64 {

    public static String base64Map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private static final char base64Pad = '=';

    private static final String hexCode = "0123456789abcdef";

    public static char int2char(int a) {
        return hexCode.charAt(a);
    }

    public static String base64ToHex(String s) {
        StringBuilder ret = new StringBuilder();
        int k = 0;
        int slop = 0;
        for (int i = 0; i < s.length(); ++ i) {
            if (s.charAt(i) == base64Pad) {
                break;
            }
            int v = base64Map.indexOf(s.charAt(i));
            if (v < 0) {
                continue;
            }
            if (k == 0) {
                ret.append(int2char(v >> 2));
                slop = v & 3;
                k = 1;
            } else if (k == 1) {
                ret.append(int2char((slop << 2) | (v >> 4)));
                slop = v & 0xf;
                k = 2;
            } else if (k == 2) {
                ret.append(int2char(slop));
                ret.append(int2char(v >> 2));
                slop = v & 3;
                k = 3;
            } else {
                ret.append(int2char((slop << 2) | (v >> 4)));
                ret.append(int2char(v & 0xf));
                k = 0;
            }
        }
        if (k == 1) {
            ret.append(int2char(slop << 2));
        }
        return ret.toString();
    }

    public static String hexToBase64(String h) {
        int i, c;
        StringBuilder ret = new StringBuilder();
        for (i = 0; i + 3 <= h.length(); i += 3) {
            c = Integer.parseInt(h.substring(i, i + 3), 16);
            ret.append(base64Map.charAt(c >> 6));
            ret.append(base64Map.charAt(c & 63));
        }
        if (i + 1 == h.length()) {
            c = Integer.parseInt(h.substring(i, i + 1), 16);
            ret.append(base64Map.charAt(c << 2));
        } else if (i + 2 == h.length()) {
            c = Integer.parseInt(h.substring(i, i + 2), 16);
            ret.append(base64Map.charAt(c >> 2));
            ret.append(base64Map.charAt((c & 3) << 4));
        }
        for (; (ret.length() & 3) > 0; ret.append(base64Pad));
        return ret.toString();
    }
}