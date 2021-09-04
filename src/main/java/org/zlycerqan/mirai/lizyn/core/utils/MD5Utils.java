package org.zlycerqan.mirai.lizyn.core.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MD5Utils {
    public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < 32 - md5code.length(); ++ i) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }

    public static String bytesToMD5(byte[] bytes) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < 32 - md5code.length(); ++ i) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }

}